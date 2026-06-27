package br.com.api.petpoints.shared.features.perfil.service;

import br.com.api.petpoints.shared.features.perfil.dto.InformacoesUsuarioDto;
import br.com.api.petpoints.shared.features.perfil.form.EditarPerfilForm;
import org.springframework.web.multipart.MultipartFile;

public interface UsuarioPerfilService {
    InformacoesUsuarioDto buscarInformacoes(Long idUsuario);
    void editarInformacoesUsuario(Long idUsuario, EditarPerfilForm form, MultipartFile imagem);
    void desativarPerfil(Long idUsuario);
}
