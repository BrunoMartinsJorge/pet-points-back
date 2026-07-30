package br.com.api.petpoints.shared.features.clientes.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioJaCadastrado;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.shared.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.shared.features.clientes.forms.RelatorioClienteClinicaForm;
import br.com.api.petpoints.shared.features.logs.LogsServiceImpl;
import br.com.api.petpoints.shared.form.RegistroForm;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientesClinicaServiceImpl implements ClientesClinicaService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final RelatoriosUtils relatoriosUtils;
    private final PasswordEncoder passwordEncoder;
    private final LogsServiceImpl logsService;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;

    @Override
    public List<ClienteDto> listarClientesClinica() {
        return ClienteDto.convert(this.usuarioRepository.findAllByPermissao(TipoUsuario.C));
    }

    @Override
    public ClientesDetalhesDto buscarDetalhesCliente(Long idCliente) {
        UsuarioModel cliente = this.usuarioRepository.findById(idCliente).orElseThrow(() -> new UsuarioNaoEncontrado("Cliente com ID: " + idCliente + " não encontrado!"));
        return new ClientesDetalhesDto(cliente);
    }

    @Override
    public List<HistoricoConsultasClienteDto> historicoConsultasCliente(Long idCliente) {
        return HistoricoConsultasClienteDto.convert(this.consultaRepository.findAllBySolicitante_Id(idCliente));
    }

    @Override
    public List<PetsClienteDto> listarPetsCliente(Long idCliente) {
        return PetsClienteDto.convert(this.petRepository.findAllByTutor_Id(idCliente));
    }

    @Override
    public byte[] gerarRelatorio(RelatorioClienteClinicaForm form) {
        List<UsuarioModel> pets = this.filtrarClientes(form);
        String titulo = "Relatório de Pets da Clínica";
        List<ColunaRelatorio> colunas = List.of(
                new ColunaRelatorio("ID", m -> ((UsuarioModel) m).getId()),
                new ColunaRelatorio("Nome", m -> ((UsuarioModel) m).getNome()),
                new ColunaRelatorio("Gênero", m -> ((UsuarioModel) m).getGenero().getDescricao()),
                new ColunaRelatorio("CPF", m -> ((UsuarioModel) m).getCpf()),
                new ColunaRelatorio("Email", m -> ((UsuarioModel) m).getEmail()),
                new ColunaRelatorio("Status Perfil", m -> ((UsuarioModel) m).getStatusPerfilEnum().getDescricao()),
                new ColunaRelatorio("Registrado Em", m -> (LocalDateTimeUtils.converterLocalDateTimeParaPtBr(((UsuarioModel) m).getDataCadastro())))
        );
        return this.relatoriosUtils.gerarRelatorioGenerico(colunas, pets, titulo, "");
    }

    @Override
    public void registrarCliente(Long idUsuario, RegistroForm form) {
        log.info("Iniciando processo de registrar novo cliente...");
        UsuarioModel usuario = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new ObjectNotFoundException("Atendente/Gerente não encontrado com o ID: " + idUsuario));
        log.info("Cliente {} sendo registrado por gerente/atendente {} ...", form.getNome(), usuario.getNome());
        if (usuarioRepository.existsByEmailOrCpf(form.getEmail(), form.getCpf()))
            throw new UsuarioJaCadastrado("Usuário já cadastrado!");
        UsuarioModel cliente = this.usuarioRepository.save(new UsuarioModel(form, TipoUsuario.C, passwordEncoder.encode(form.getSenha())));
        log.info("Novo cliente registrado com sucesso!");
        this.logsService.registrarLog(usuario, TipoLogEnum.ADICIONOU_CLIENTE);
        this.enviarEmailRegistro(cliente, form.getSenha());
    }

    private void enviarEmailRegistro(UsuarioModel cliente, String senha) {
        try {
            Context context = new Context();
            context.setVariable("usuario", cliente.getNome());
            context.setVariable("senha", senha);
            String htmlTemplate = templateEngine.process("email/usuario_registrado", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(cliente.getEmail());
            helper.setSubject("Cliente Registrado");
            helper.setFrom("clinapetpoints@gmail.com");
            helper.setText(htmlTemplate, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar email");
        }
        log.info("Email para o cliente" + cliente.getNome() + " enviado com sucesso!");
    }

    private List<UsuarioModel> filtrarClientes(RelatorioClienteClinicaForm form) {
        List<UsuarioModel> pets = this.usuarioRepository.findAllByPermissao(TipoUsuario.C);
        if (!Objects.equals(form.getNome(), ""))
            pets = pets.stream().filter(pet -> pet.getNome().contains(form.getNome())).toList();
        if (!Objects.equals(form.getGenero(), ""))
            pets = pets.stream().filter(pet -> pet.getGenero().toString().equals(form.getGenero())).toList();
        return pets;
    }
}
