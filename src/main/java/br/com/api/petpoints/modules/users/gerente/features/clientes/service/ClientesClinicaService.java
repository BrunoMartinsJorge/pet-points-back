package br.com.api.petpoints.modules.users.gerente.features.clientes.service;

import br.com.api.petpoints.modules.users.gerente.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.modules.users.gerente.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.modules.users.gerente.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.modules.users.gerente.features.clientes.dto.PetsClienteDto;

import java.util.List;

public interface ClientesClinicaService {
    List<ClienteDto> listarClientesClinica();
    ClientesDetalhesDto buscarDetalhesCliente(Long idCliente);
    List<HistoricoConsultasClienteDto> historicoConsultasCliente(Long idCliente);
    List<PetsClienteDto> listarPetsCliente(Long idCliente);
}
