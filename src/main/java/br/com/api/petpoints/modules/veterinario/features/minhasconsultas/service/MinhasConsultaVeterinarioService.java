package br.com.api.petpoints.modules.veterinario.features.minhasconsultas.service;

import br.com.api.petpoints.modules.veterinario.features.minhasconsultas.dto.ConsultaDto;

import java.util.List;

public interface MinhasConsultaVeterinarioService {

    List<ConsultaDto> listarMinhasConsultas(Long idUsuario);
    List<ConsultaDto> listarMinhasConsultasDoDia(Long idUsuario);
    void iniciarConsulta(Long idUsuario, Long idConsulta);
    void finalizarConsulta(Long idUsuario, Long idConsulta, String resumo);
    Object gerarPrescricao(Long idUsuario, Long idConsulta);
}
