package br.com.api.petpoints.modules.users.gerente.features.consultas.service;

import br.com.api.petpoints.modules.users.gerente.features.consultas.dto.*;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.EspecializacaoForm;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.FiltroConsultaForm;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.TipoConsultaForm;

import java.util.List;

public interface GerenteConsultasClinicaService {
    List<ConsultaClinicaDto> listarHistoricoConsultas();
    List<ParticipanteFiltrosDto> listarSolicitantesParaFiltros();
    List<ParticipanteFiltrosDto> listarVeterinariosParaFiltros();
    List<TipoConsultaFiltrosDto> listarTiposConsultasParaFiltros();
    void adicionarNovaEspecializacao(EspecializacaoForm form);
    List<EspecializacoesDto> listarEspecializacoes();
    DetalhesEspecialziacaoDto buscarDetalhesEspecializacoes(Long id);
    List<TiposConsultaDto> listarTiposConsulta();
    DetalhesConsultaDto buscarDetalhesConsulta(Long idConsulta);
    DetalhesTipoConsultaDto buscarDetalhesTipoConsulta(Long idTipoConsulta);
    void adicionarNovoTipoConsulta(Long idUsuario, TipoConsultaForm form);
    void editarInformacoesTipoConsulta(Long idUsuario, TipoConsultaForm form, Long idTipoConsulta);
    List<VeterinarioEspecializacoesDto> listarVeterinariosTipoConsulta(Long idTipoConsulta);
    void adicionarNovoVeterinarioTipoConsulta(Long idVeterinario, Long idTipoConsulta);
    void removerNovoVeterinarioTipoConsulta(Long idVeterinario, Long idTipoConsulta);
    byte[] gerarRelatorioConsultas(FiltroConsultaForm form);
    void adicionarNovoVeteterinarioEspecializacao(Long idEspecializacao, Long idVeterinario);
    void removerVeteterinarioEspecializacao(Long idEspecializacao, Long idVeterinario);
}
