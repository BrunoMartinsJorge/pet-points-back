package br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.service;

import br.com.api.petpoints.modules.auth.exception.UsuarioNaoEncontrado;
import br.com.api.petpoints.modules.users.cliente.features.meuspagamentos.dto.PagamentosPendentesDto;
import br.com.api.petpoints.shared.exception.custom.ObjectNotFoundException;
import br.com.api.petpoints.shared.models.*;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import br.com.api.petpoints.shared.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeusPagamentosServiceImpl implements MeusPagamentosService {

    private final ConsultaRepository consultaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;

    private UsuarioModel getUsuarioPorId(Long idUsuario) {
        return this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário com ID: " + idUsuario));
    }

    private ConsultaModel getConsultaPorId(Long idConsulta) {
        return this.consultaRepository.findById(idConsulta).orElseThrow(() -> new ObjectNotFoundException("Consulta com ID: " + idConsulta + " não encontrada!"));
    }


    @Override
    public List<PagamentosPendentesDto> listarPagamentosPendentes(Long idUsuario) {
        List<ConsultaModel> consultas = this.consultaRepository.buscarConsultasPorUsuarioComPagamentosPendentes(idUsuario);
        return PagamentosPendentesDto.convert(consultas);
    }
}
