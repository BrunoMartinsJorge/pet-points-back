package br.com.api.petpoints.modules.gerente.features.funcionarios.services;

import br.com.api.petpoints.modules.gerente.features.funcionarios.dto.FuncionarioDto;
import br.com.api.petpoints.modules.gerente.features.funcionarios.forms.FuncionarioForm;

import java.util.List;

public interface FuncionariosGerenteService {
    List<FuncionarioDto> listarFuncionarios(Long idUsuario);
    FuncionarioDto buscarFuncionarioPorId(Long idFuncionario);
    FuncionarioDto cadastrarNovoFuncionario(FuncionarioForm form);
    FuncionarioDto atualizarFuncionario(FuncionarioForm form, Long idFuncionario);
    void desativarFuncionario(Long idUsuario, Long idFuncionario);
}
