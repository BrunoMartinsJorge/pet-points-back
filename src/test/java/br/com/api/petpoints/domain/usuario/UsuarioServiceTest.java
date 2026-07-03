package br.com.api.petpoints.domain.usuario;

import br.com.api.petpoints.shared.models.UsuarioModel;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void naoPermitirEmailDuplicado(){
        when(usuarioRepository.existsByEmail("teste@gmail.com")).thenReturn(true);
        UsuarioModel usuarioModel = new UsuarioModel();

    }
}
