package br.com.api.petpoints.modules.users.cliente.features.perfil.service;

import br.com.api.petpoints.modules.users.cliente.features.perfil.dto.InformacoesUsuarioDto;
import br.com.api.petpoints.modules.users.cliente.features.perfil.form.EditarPerfilFotm;
import org.springframework.web.multipart.MultipartFile;

public interface ClientePerfilService {
    InformacoesUsuarioDto buscarInformacoes(Long idUsuario);
    void editarInformacoesUsuario(Long idUsuario, EditarPerfilFotm form, MultipartFile imagem);
    void desativarPerfil(Long idUsuario);
}
