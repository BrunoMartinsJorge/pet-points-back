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

import java.math.BigDecimal;
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
    private final PetRepository petRepository;

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
                "Bruno Martins Jorge", "gerente@gmail.com",
                GeneroEnum.M, "2006-06-12", "18996313182", "48250377044", TipoUsuario.G));

        criados.put("veterinario", salvarUsuario(
                "Carlos Eduardo Zaul", "usuario@gmail.com",
                GeneroEnum.M, "2005-08-21", "18996413456", "22307089020", TipoUsuario.V));

        criados.put("atendente", salvarUsuario(
                "Arthur Moura Rieger", "atendente@gmail.com",
                GeneroEnum.M, "2006-11-26", "18996312459", "28449414008", TipoUsuario.A));

        criados.put("cliente", salvarUsuario(
                "Marizinha", "rexon300008@gmail.com",
                GeneroEnum.F, "1980-10-16", "18996738459", "57700774846", TipoUsuario.C));

        pet(criados.get("cliente"));

        criados.put("cliente_pagamento", salvarUsuario(
                "Cliente Teste Pagamento", "test_user_1724857044@testuser.com",
                GeneroEnum.F, "1980-10-16", "18996738459", "19119119100", TipoUsuario.C));

        pet(criados.get("cliente_pagamento"));

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

    @Transactional
    protected void mockarConsulta(UsuarioModel cliente, UsuarioModel atendente, UsuarioModel veterinario, TipoConsultaModel tipo) {
        ConsultaModel consulta = new ConsultaModel();
        consulta.setDataConsulta(LocalDateTime.now().minusDays(1));
        consulta.setVeterinario(veterinario);
        consulta.setTipoConsulta(tipo);
        PetModel pet = this.petRepository.findAllByTutor_Id(cliente.getId()).getFirst();
        consulta.setPet(pet);
        consulta.setStatus(StatusConsultaEnum.PENDENTE);
        consulta.setSolicitante(cliente);
        // consulta.setAtendente(atendente);
        consulta.setSolicitadoEm(LocalDateTime.now().minusDays(2));
        consulta.setFormaPagamento(TipoPagamentoEnum.CARTAO);
        this.consultaRepository.save(consulta);
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
        rotina.setValor(1.00);
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

        this.tipoConsultaRepository.saveAll(List.of(emergencia, vacinacao));
        rotina = this.tipoConsultaRepository.save(rotina);
        // mockarConsulta(usuarios.get("cliente"), usuarios.get("atendente"), usuarios.get("veterinario"), rotina);
    }

    private void pet(UsuarioModel cliente) {
        List<PetModel> petsCliente = this.petRepository.findAllByTutor_Id(cliente.getId());
        if (!petsCliente.isEmpty()) return;
        PetModel pet = new PetModel();
        pet.setTutor(cliente);
        pet.setStatus(StatusPerfilEnum.A);
        pet.setGenero(GeneroEnum.F);
        pet.setDataNascimento(LocalDate.now());
        pet.setNome("Mel");
        pet.setRaca("Shitzu");
        pet.setTipo(TipoAnimalEnum.CACHORRO);
        this.petRepository.save(pet);
    }

    private void pagamentos() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByPagamentoIsNull();
        for (ConsultaModel consulta : consultas) {
            PagamentoModel pagamento = new PagamentoModel();
            pagamento.setTipoPagamento(TipoPagamentoEnum.PIX);
            pagamento.setDataLimitePagamento(consulta.getDataConsulta().plusWeeks(2));
            pagamento.setValorPagamento(consulta.getTipoConsulta() != null ? BigDecimal.valueOf(consulta.getTipoConsulta().getValor()) : BigDecimal.ZERO);
            pagamento.setEmitidoPor(consulta.getSolicitante());
            pagamento = this.pagamentoRepository.save(pagamento);
            consulta.setPagamento(pagamento);
            this.consultaRepository.save(consulta);
        }
    }
}