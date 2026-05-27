package br.com.api.petpoints.modules.users.cliente.features.meusatendimentos.services;

import br.com.api.petpoints.modules.users.cliente.features.meusatendimentos.dto.MeusAtendimentosDto;
import br.com.api.petpoints.shared.models.AtendimentoModel;
import br.com.api.petpoints.shared.repository.AtendimentoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeusAtendimentosClienteServiceImpl implements MeusAtendimentosClienteService {

    private final UsuarioRepository usuarioRepository;
    private final AtendimentoRepository atendimentoRepository;

    @Override
    public List<MeusAtendimentosDto> listarMeusAtendimentos(Long idUsuario) {
        List<AtendimentoModel> atendimentos = this.atendimentoRepository.findAllByCliente_Id(idUsuario);
        return MeusAtendimentosDto.convert(atendimentos);
    }
}
