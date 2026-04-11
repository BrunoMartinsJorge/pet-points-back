package br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.service;

import br.com.api.petpoints.modules.users.estoquista.features.movimentacoes.dto.MinhasMovimentacoesDto;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinhasMovimentacoesServiceImpl implements MinhasMovimentacoesService {

    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TemplateEngine templateEngine;

    @Override
    public List<MinhasMovimentacoesDto> listarMovimentacoesEstoquista(Long idUsuario) {
        return MinhasMovimentacoesDto.convert(this.movimentacaoRepository.findAllByMovimentadoPor_Id(idUsuario));
    }
}
