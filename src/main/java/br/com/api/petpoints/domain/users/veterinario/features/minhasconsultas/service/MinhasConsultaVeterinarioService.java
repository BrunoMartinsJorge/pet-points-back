package br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.service;

import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaAtualDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ConsultaVeterinarioDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.InformacoesConsultaSelecionadaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.dto.ProdutoCobrancaDto;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.FinalizarConsultaForm;
import br.com.api.petpoints.domain.users.veterinario.features.minhasconsultas.forms.PrescricaoForm;

import java.util.List;

public interface MinhasConsultaVeterinarioService {

    List<ConsultaVeterinarioDto> listarMinhasConsultas(Long idUsuario);
    List<ConsultaVeterinarioDto> listarMinhasConsultasDoDia(Long idUsuario);
    ConsultaAtualDto buscarConsultaAtualVeterinario(Long idUsuario);
    InformacoesConsultaSelecionadaDto buscarInformacoesConsulta(Long idConsulta, Long idUsuario);
    List<ProdutoCobrancaDto> listarProdutosParaCobranca();
    void iniciarConsulta(Long idUsuario, Long idConsulta);
    void finalizarConsulta(Long idUsuario, Long idConsulta, FinalizarConsultaForm form);
    Object gerarPrescricao(Long idUsuario, PrescricaoForm form);
}
