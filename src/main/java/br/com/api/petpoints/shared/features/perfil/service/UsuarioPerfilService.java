package br.com.api.petpoints.shared.features.perfil.service;

import br.com.api.petpoints.domain.users.atendente.features.consultas.dto.AvaliacaoConsultaDto;
import br.com.api.petpoints.shared.features.perfil.dto.*;
import br.com.api.petpoints.shared.features.perfil.form.EditarPerfilForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsuarioPerfilService {
    InformacoesUsuarioDto buscarInformacoes(Long idUsuario);
    void editarInformacoesUsuario(Long idUsuario, EditarPerfilForm form, MultipartFile imagem);
    void desativarPerfil(Long idUsuario);
    RankingFuncionarioDto buscarInformacoesRankingAvaliacoes(Long idUsuario);
    List<AvaliacaoConsultaDto> buscarAvaliacoes(Long idUsuario);
    List<ConsultasAtendenteVeterinarioDto> buscarConsultasAtendenteVeterinario(Long idUsuario);
    RelatorioFinanceiroClienteDto buscarRelatorioFinanceiroCliente(Long idUsuario);
    List<MinhasAvaliacoesDto> buscarMinhasAvaliacoes(Long idUsuario);
}
