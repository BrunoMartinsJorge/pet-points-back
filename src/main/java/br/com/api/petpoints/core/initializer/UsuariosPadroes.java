package br.com.api.petpoints.core.initializer;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.shared.enums.*;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class UsuariosPadroes implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EspecializacaoRepository especializacaoRepository;
    private final TipoConsultaRepository tipoConsultaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PagamentoRepository pagamentoRepository;
    private final ConsultaRepository consultaRepository;

    // Repositórios adicionais necessários para os novos mocks.
    // Ajuste os nomes caso os seus repositórios tenham nomenclatura diferente.
    private final PetRepository petRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    @Override
    @NullMarked
    public void run(String... args) throws Exception {
        Map<String, UsuarioModel> usuarios = this.usuarios();

        this.especializacoes(usuarios);
        this.tiposConsulta(usuarios);
        this.pets(usuarios);
        this.produtos();
        this.movimentacoes(usuarios);
        this.notificacoes(usuarios);
        this.consultas(usuarios);
        // this.pagamentos();
    }

    /**
     * Cria os usuários padrão e devolve um mapa (email -> usuário salvo)
     * para reutilização nos demais mocks.
     */
    private Map<String, UsuarioModel> usuarios() {
        Map<String, UsuarioModel> criados = new HashMap<>();

        criados.put("gerente", salvarUsuario(
                "Bruno Martins Jorge", "rexon300008@gmail.com",
                GeneroEnum.M, "2006-06-12", "18996313182", "48250377044", TipoUsuario.G));

        criados.put("veterinario", salvarUsuario(
                "Carlos Eduardo Zaul", "usuario@gmail.com",
                GeneroEnum.M, "2005-08-21", "18996413456", "22307089020", TipoUsuario.V));

        criados.put("atendente", salvarUsuario(
                "Arthur Moura Rieger", "atendente@gmail.com",
                GeneroEnum.M, "2006-11-26", "18996312459", "28449414008", TipoUsuario.A));

        criados.put("cliente", salvarUsuario(
                "Marizinha", "cliente@gmail.com",
                GeneroEnum.F, "1980-10-16", "18996738459", "60099673096", TipoUsuario.C));

        criados.put("estoquista", salvarUsuario(
                "Yann", "estoquista@gmail.com",
                GeneroEnum.M, "2002-01-06", "18996897425", "24711763058", TipoUsuario.E));

        // Alguns usuários extras para dar volume aos dados
        criados.put("veterinario2", salvarUsuario(
                "Fernanda Lima Costa", "veterinario2@gmail.com",
                GeneroEnum.F, "1990-03-14", "18996111222", "39053344705", TipoUsuario.V));

        criados.put("cliente2", salvarUsuario(
                "João Pereira Alves", "cliente2@gmail.com",
                GeneroEnum.M, "1995-07-02", "18996333444", "45317828791", TipoUsuario.C));

        return criados;
    }

    private UsuarioModel salvarUsuario(String nome, String email, GeneroEnum genero,
                                       String nascimento, String telefone,
                                       String cpf, TipoUsuario permissao) {
        UsuarioModel existente = this.usuarioRepository.findByEmail(email).orElse(null);
        if (existente != null) {
            return existente;
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(this.passwordEncoder.encode("123456"));
        usuario.setGenero(genero);
        usuario.setDataNascimento(LocalDate.parse(nascimento));
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setPermissao(permissao);

        if (!this.usuarioRepository.existsByEmailOrCpf(usuario.getEmail(), usuario.getCpf())) {
            return this.usuarioRepository.save(usuario);
        }
        return this.usuarioRepository.findByEmail(email).orElse(usuario);
    }

    private void especializacoes(Map<String, UsuarioModel> usuarios) {
        if (this.especializacaoRepository.count() > 0) return;

        Set<UsuarioModel> vets = new HashSet<>();
        vets.add(usuarios.get("veterinario"));
        vets.add(usuarios.get("veterinario2"));

        EspecializacaoModel clinicaGeral = new EspecializacaoModel();
        clinicaGeral.setDescricao("Clínica Geral");
        clinicaGeral.setVeterinarios(vets);

        EspecializacaoModel dermatologia = new EspecializacaoModel();
        dermatologia.setDescricao("Dermatologia Veterinária");
        dermatologia.setVeterinarios(Set.of(usuarios.get("veterinario2")));

        EspecializacaoModel cardiologia = new EspecializacaoModel();
        cardiologia.setDescricao("Cardiologia Veterinária");
        cardiologia.setVeterinarios(Set.of(usuarios.get("veterinario")));

        this.especializacaoRepository.saveAll(List.of(clinicaGeral, dermatologia, cardiologia));
    }

    private void tiposConsulta(Map<String, UsuarioModel> usuarios) {
        if (this.tipoConsultaRepository.count() > 0) return;

        List<UsuarioModel> vets = List.of(
                usuarios.get("veterinario"),
                usuarios.get("veterinario2"));

        TipoConsultaModel rotina = new TipoConsultaModel();
        rotina.setNome("Consulta de Rotina");
        rotina.setDescricao("Avaliação geral de saúde do animal.");
        rotina.setValor(120.00);
        rotina.setVeterinarios(vets);

        TipoConsultaModel emergencia = new TipoConsultaModel();
        emergencia.setNome("Emergência");
        emergencia.setDescricao("Atendimento imediato para casos urgentes.");
        emergencia.setValor(250.00);
        emergencia.setVeterinarios(vets);

        TipoConsultaModel vacinacao = new TipoConsultaModel();
        vacinacao.setNome("Vacinação");
        vacinacao.setDescricao("Aplicação de vacinas e orientação do calendário vacinal.");
        vacinacao.setValor(80.00);
        vacinacao.setVeterinarios(List.of(usuarios.get("veterinario")));

        this.tipoConsultaRepository.saveAll(List.of(rotina, emergencia, vacinacao));
    }

    private void pets(Map<String, UsuarioModel> usuarios) {
        if (this.petRepository.count() > 0) return;

        UsuarioModel cliente = usuarios.get("cliente");
        UsuarioModel cliente2 = usuarios.get("cliente2");

        PetModel rex = new PetModel();
        rex.setNome("Rex");
        rex.setGenero(GeneroEnum.M);
        rex.setDataNascimento(LocalDate.parse("2020-05-10"));
        rex.setTipo(TipoAnimalEnum.CACHORRO); // AJUSTE conforme seu enum
        rex.setRaca("Labrador");
        rex.setObservacoes("Alérgico a alguns tipos de ração.");
        rex.setTutor(cliente);

        PetModel mimi = new PetModel();
        mimi.setNome("Mimi");
        mimi.setGenero(GeneroEnum.F);
        mimi.setDataNascimento(LocalDate.parse("2021-09-22"));
        mimi.setTipo(TipoAnimalEnum.GATO); // AJUSTE conforme seu enum
        mimi.setRaca("Siamês");
        mimi.setObservacoes("Muito arisca com estranhos.");
        mimi.setTutor(cliente);

        PetModel thor = new PetModel();
        thor.setNome("Thor");
        thor.setGenero(GeneroEnum.M);
        thor.setDataNascimento(LocalDate.parse("2019-01-15"));
        thor.setTipo(TipoAnimalEnum.CACHORRO); // AJUSTE conforme seu enum
        thor.setRaca("Bulldog");
        thor.setObservacoes("Histórico de problemas respiratórios.");
        thor.setTutor(cliente2);

        this.petRepository.saveAll(List.of(rex, mimi, thor));
    }

    private void produtos() {
        if (this.produtoRepository.count() > 0) return;

        ProdutoModel racao = new ProdutoModel();
        racao.setNome("Ração Premium Cães Adultos 15kg");
        racao.setDescricao("Ração super premium para cães adultos de porte médio.");
        racao.setTipo(TipoProdutoEnum.RACAO); // AJUSTE conforme seu enum
        racao.setValorUnitario(189.90);
        racao.setQuantidadeEstoque(50);
        racao.setQuantidadeMinima(10);

        ProdutoModel vermifugo = new ProdutoModel();
        vermifugo.setNome("Vermífugo Cães e Gatos");
        vermifugo.setDescricao("Antiparasitário de amplo espectro.");
        vermifugo.setTipo(TipoProdutoEnum.HIGIENE); // AJUSTE conforme seu enum
        vermifugo.setValorUnitario(45.50);
        vermifugo.setQuantidadeEstoque(120);
        vermifugo.setQuantidadeMinima(30);

        ProdutoModel shampoo = new ProdutoModel();
        shampoo.setNome("Shampoo Antipulgas 500ml");
        shampoo.setDescricao("Shampoo com ação antipulgas e antisséptica.");
        shampoo.setTipo(TipoProdutoEnum.HIGIENE); // AJUSTE conforme seu enum
        shampoo.setValorUnitario(32.00);
        shampoo.setQuantidadeEstoque(8);
        shampoo.setQuantidadeMinima(15);

        ProdutoModel brinquedo = new ProdutoModel();
        brinquedo.setNome("Brinquedo Mordedor de Borracha");
        brinquedo.setDescricao("Brinquedo resistente para cães de todos os portes.");
        brinquedo.setTipo(TipoProdutoEnum.BRINQUEDO); // AJUSTE conforme seu enum
        brinquedo.setValorUnitario(29.90);
        brinquedo.setQuantidadeEstoque(75);
        brinquedo.setQuantidadeMinima(20);

        this.produtoRepository.saveAll(List.of(racao, vermifugo, shampoo, brinquedo));
    }

    private void movimentacoes(Map<String, UsuarioModel> usuarios) {
        if (this.movimentacaoRepository.count() > 0) return;

        UsuarioModel estoquista = usuarios.get("estoquista");
        List<ProdutoModel> produtos = this.produtoRepository.findAll();
        if (produtos.isEmpty()) return;

        for (ProdutoModel produto : produtos) {
            MovimentacaoModel entrada = new MovimentacaoModel();
            entrada.setProduto(produto);
            entrada.setMovimentadoPor(estoquista);
            entrada.setTipo(TipoMovimentacaoEnum.ENTRADA); // AJUSTE conforme seu enum
            entrada.setQuantidadeMovimentada(20);
            this.movimentacaoRepository.save(entrada);
        }

        MovimentacaoModel saida = new MovimentacaoModel();
        saida.setProduto(produtos.get(0));
        saida.setMovimentadoPor(estoquista);
        saida.setTipo(TipoMovimentacaoEnum.SAIDA); // AJUSTE conforme seu enum
        saida.setQuantidadeMovimentada(5);
        this.movimentacaoRepository.save(saida);
    }

    private void notificacoes(Map<String, UsuarioModel> usuarios) {
        if (this.notificacaoRepository.count() > 0) return;

        NotificacaoModel n1 = new NotificacaoModel();
        n1.setPara(usuarios.get("cliente"));
        n1.setTitulo("Bem-vindo(a) ao PetPoints!");
        n1.setConteudo("Sua conta foi criada com sucesso. Aproveite nossos serviços.");
        n1.setTipo(TiposNotificacoesEnum.MENSAGEM); // AJUSTE conforme seu enum
        n1.setVisto(false);

        NotificacaoModel n2 = new NotificacaoModel();
        n2.setPara(usuarios.get("veterinario"));
        n2.setTitulo("Nova consulta agendada");
        n2.setConteudo("Você possui uma nova consulta pendente de atendimento.");
        n2.setTipo(TiposNotificacoesEnum.CONSULTA); // AJUSTE conforme seu enum
        n2.setVisto(false);

        NotificacaoModel n3 = new NotificacaoModel();
        n3.setPara(usuarios.get("estoquista"));
        n3.setTitulo("Estoque baixo");
        n3.setConteudo("O produto 'Shampoo Antipulgas 500ml' está abaixo da quantidade mínima.");
        n3.setTipo(TiposNotificacoesEnum.ALERTA); // AJUSTE conforme seu enum
        n3.setVisto(false);

        this.notificacaoRepository.saveAll(List.of(n1, n2, n3));
    }

    private void consultas(Map<String, UsuarioModel> usuarios) {
        if (this.consultaRepository.count() > 0) return;

        UsuarioModel cliente = usuarios.get("cliente");
        UsuarioModel atendente = usuarios.get("atendente");
        UsuarioModel veterinario = usuarios.get("veterinario");

        List<PetModel> pets = this.petRepository.findAll();
        List<TipoConsultaModel> tipos = this.tipoConsultaRepository.findAll();
        if (pets.isEmpty() || tipos.isEmpty()) return;

        PetModel pet = pets.get(0);
        TipoConsultaModel tipoConsulta = tipos.get(0);

        // Consulta 1 - PENDENTE (sem pagamento)
        ConsultaModel pendente = new ConsultaModel();
        pendente.setSolicitante(cliente);
        pendente.setPet(pet);
        pendente.setTipoConsulta(tipoConsulta);
        pendente.setStatus(StatusConsultaEnum.PENDENTE);
        pendente.setObservacoes("Pet apresentando falta de apetite.");
        this.consultaRepository.save(pendente);

        // Consulta 2 - DEFERIDA com pagamento e avaliação
        ConsultaModel deferida = new ConsultaModel();
        deferida.setSolicitante(cliente);
        deferida.setAtendente(atendente);
        deferida.setVeterinario(veterinario);
        deferida.setPet(pet);
        deferida.setTipoConsulta(tipoConsulta);
        deferida.setStatus(StatusConsultaEnum.APROVADA); // AJUSTE conforme seu enum
        deferida.setDeferidoEm(LocalDateTime.now().minusDays(3));
        deferida.setDataConsulta(LocalDateTime.now().plusDays(2));
        deferida.setObservacoes("Consulta de rotina agendada.");

        PagamentoModel pagamento = new PagamentoModel();
        pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);
        pagamento.setValorPagamento(tipoConsulta.getValor());
        pagamento.setDataLimitePagamento(LocalDateTime.now().plusWeeks(2));
        pagamento.setEmitidoPor(cliente);
        deferida.setPagamento(pagamento);

        this.consultaRepository.save(deferida);

        // Consulta 3 - FINALIZADA com resumo e avaliação
        AvaliacaoModel avaliacao = new AvaliacaoModel();
        avaliacao.setPontuacao(5);
        avaliacao.setAvaliadoPor(cliente);
        avaliacao.setObservacoes("Atendimento excelente, veterinário muito atencioso!");

        ConsultaModel finalizada = new ConsultaModel();
        finalizada.setSolicitante(cliente);
        finalizada.setAtendente(atendente);
        finalizada.setVeterinario(veterinario);
        finalizada.setPet(pet);
        finalizada.setTipoConsulta(tipoConsulta);
        finalizada.setStatus(StatusConsultaEnum.FINALIZADO); // AJUSTE conforme seu enum
        finalizada.setDeferidoEm(LocalDateTime.now().minusDays(10));
        finalizada.setDataConsulta(LocalDateTime.now().minusDays(7));
        finalizada.setIniciadoEm(LocalDateTime.now().minusDays(7));
        finalizada.setFinalizadoEm(LocalDateTime.now().minusDays(7).plusHours(1));
        finalizada.setResumoConsulta("Animal saudável. Recomendada nova avaliação em 6 meses.");
        finalizada.setAvaliacao(avaliacao);

        PagamentoModel pagamentoPago = new PagamentoModel();
        pagamentoPago.setTipoPagamento(TipoPagamentoEnum.PIX);
        pagamentoPago.setValorPagamento(tipoConsulta.getValor());
        pagamentoPago.setDataLimitePagamento(LocalDateTime.now().minusDays(2));
        pagamentoPago.setStatusPagamento(StatusPagamentoEnum.APROVADO); // AJUSTE conforme seu enum
        pagamentoPago.setEmitidoPor(cliente);
        pagamentoPago.setAprovadoPor(atendente);
        finalizada.setPagamento(pagamentoPago);

        this.consultaRepository.save(finalizada);
    }

    private void pagamentos() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByPagamentoIsNull();
        for (ConsultaModel consulta : consultas) {
            PagamentoModel pagamento = new PagamentoModel();
            pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);
            pagamento.setDataLimitePagamento(consulta.getDataConsulta().plusWeeks(2));
            pagamento.setValorPagamento(consulta.getTipoConsulta() != null ? consulta.getTipoConsulta().getValor() : 0);
            pagamento.setEmitidoPor(consulta.getSolicitante());
            pagamento = this.pagamentoRepository.save(pagamento);
            consulta.setPagamento(pagamento);
            this.consultaRepository.save(consulta);
        }
    }
}