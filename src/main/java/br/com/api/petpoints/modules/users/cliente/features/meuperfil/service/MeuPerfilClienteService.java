package br.com.api.petpoints.modules.users.cliente.features.meuperfil.service;

import br.com.api.petpoints.modules.users.cliente.features.meuperfil.dto.MeuPerfilDto;
import br.com.api.petpoints.modules.users.cliente.features.meuperfil.forms.EditarPerfilClienteForm;

public interface MeuPerfilClienteService {

    MeuPerfilDto buscarPerfilDeCliente(Long idUsuario);
    void editarPerfilCliente(Long idUsuario, EditarPerfilClienteForm form);
    void desativarPerfilDeCliente(Long idUsuario);
}
