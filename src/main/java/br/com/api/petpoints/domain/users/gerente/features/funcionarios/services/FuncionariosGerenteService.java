package br.com.api.petpoints.domain.users.gerente.features.funcionarios.services;

import br.com.api.petpoints.shared.dto.AvaliacoesDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.ConsultaFuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.dto.MovimentacoesEstoquistasDto;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.EditarFuncionarioForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FiltroFuncionariosForm;
import br.com.api.petpoints.domain.users.gerente.features.funcionarios.forms.FuncionarioForm;
import br.com.api.petpoints.shared.dto.OpcoesDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface FuncionariosGerenteService {
    List<FuncionarioDto> listarFuncionarios(Long idUsuario);
    FuncionarioDto buscarFuncionarioPorId(Long idFuncionario);
    Set<OpcoesDto> listarEspecializacoes();
    FuncionarioDto cadastrarNovoFuncionario(FuncionarioForm form);
    FuncionarioDto atualizarFuncionario(EditarFuncionarioForm form, Long idFuncionario, MultipartFile file);
    void desativarFuncionario(Long idUsuario, Long idFuncionario);
    byte[] gerarRelatorioFuncionarios(FiltroFuncionariosForm form);
    List<AvaliacoesDto> buscarAvaliacoesPorId(Long id);
    List<ConsultaFuncionarioDto> buscarConsultasPorId(Long id);
    List<MovimentacoesEstoquistasDto> buscarMovimentacoesPorId(Long id);
}
