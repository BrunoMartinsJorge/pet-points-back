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

    @Transactional
    @Override
    @NullMarked
    public void run(String... args) throws Exception {
        Map<String, UsuarioModel> usuarios = this.usuarios();

        this.especializacoes(usuarios);
        this.tiposConsulta(usuarios);
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