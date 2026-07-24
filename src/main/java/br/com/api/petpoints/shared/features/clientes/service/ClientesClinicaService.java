package br.com.api.petpoints.shared.features.clientes.service;

import br.com.api.petpoints.shared.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.ClientesDetalhesDto;
import br.com.api.petpoints.shared.features.clientes.dto.HistoricoConsultasClienteDto;
import br.com.api.petpoints.shared.features.clientes.dto.PetsClienteDto;
import br.com.api.petpoints.shared.features.clientes.forms.RelatorioClienteClinicaForm;

import java.util.List;

public interface ClientesClinicaService {
    List<ClienteDto> listarClientesClinica();
    ClientesDetalhesDto buscarDetalhesCliente(Long idCliente);
    List<HistoricoConsultasClienteDto> historicoConsultasCliente(Long idCliente);
    List<PetsClienteDto> listarPetsCliente(Long idCliente);
    byte[] gerarRelatorio(RelatorioClienteClinicaForm form);
}
