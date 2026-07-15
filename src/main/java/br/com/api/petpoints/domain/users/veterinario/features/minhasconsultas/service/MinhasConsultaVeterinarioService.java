package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service;

import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaVeterinarioDto;

import java.util.List;

public interface MinhasConsultaVeterinarioService {

    List<ConsultaVeterinarioDto> listarMinhasConsultas(Long idUsuario);
    List<ConsultaVeterinarioDto> listarMinhasConsultasDoDia(Long idUsuario);
    void iniciarConsulta(Long idUsuario, Long idConsulta);
    void finalizarConsulta(Long idUsuario, Long idConsulta, String resumo);
    Object gerarPrescricao(Long idUsuario, Long idConsulta);
}
