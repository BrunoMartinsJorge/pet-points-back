package br.com.api.petpoints.shared.features.clientes.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.gerente.features.pets.form.RelatorioPetsClinicaForm;
import br.com.api.petpoints.shared.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.shared.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.shared.features.clientes.forms.RelatorioClienteClinicaForm;
import br.com.api.petpoints.shared.models.PetModel;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import br.com.api.petpoints.shared.utils.ColunaRelatorio;
import br.com.api.petpoints.shared.utils.LocalDateTimeUtils;
import br.com.api.petpoints.shared.utils.RelatoriosUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientesClinicaServiceImpl implements ClientesClinicaService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final RelatoriosUtils relatoriosUtils;

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

    private List<UsuarioModel> filtrarClientes(RelatorioClienteClinicaForm form) {
        List<UsuarioModel> pets = this.usuarioRepository.findAllByPermissao(TipoUsuario.C);
        if (!Objects.equals(form.getNome(), ""))
            pets = pets.stream().filter(pet -> pet.getNome().contains(form.getNome())).toList();
        if (!Objects.equals(form.getGenero(), ""))
            pets = pets.stream().filter(pet -> pet.getGenero().toString().equals(form.getGenero())).toList();
        return pets;
    }
}
