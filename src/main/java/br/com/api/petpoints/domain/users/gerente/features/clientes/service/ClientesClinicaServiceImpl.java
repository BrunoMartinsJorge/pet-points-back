package br.com.api.petpoints.domain.users.gerente.features.clientes.service;

import br.com.api.petpoints.core.token.TipoUsuario;
import br.com.api.petpoints.domain.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.domain.users.gerente.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PetRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientesClinicaServiceImpl implements ClientesClinicaService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;

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
}
