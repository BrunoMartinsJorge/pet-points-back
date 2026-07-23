package br.com.api.petpoints.domain.users.gerente.features.movimentacoes.service;

import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.LancadoPorDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.dto.MovimentacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.movimentacoes.form.FiltroMovimentacoesForm;
import br.com.api.petpoints.shared.dto.ProdutoFiltroDto;

import java.util.List;

public interface MovimentacoesClinicaService {
    List<MovimentacoesDto> listarMovimentacoes();
    List<ProdutoFiltroDto> listarProdutosFiltro();
    List<LancadoPorDto> listarEstoquistasFiltro();
    byte[] gerarRelatorioMovimentacoes(Long idUsuario, FiltroMovimentacoesForm form);
}
