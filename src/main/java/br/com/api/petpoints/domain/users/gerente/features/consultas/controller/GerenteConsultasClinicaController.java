package br.com.api.petpoints.domain.users.gerente.features.consultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.domain.users.gerente.features.consultas.dto.*;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.EspecializacaoForm;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.FiltroConsultaForm;
import br.com.api.petpoints.domain.users.gerente.features.consultas.form.TipoConsultaForm;
import br.com.api.petpoints.domain.users.gerente.features.consultas.service.GerenteConsultasClinicaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente/consultas-clinica")
@RequiredArgsConstructor
@Tag(
        name = "Consultas da Clínica - Gerente",
        description = "Endpoints destinados ao gerenciamento de consultas clínicas, "
                + "tipos de consultas, especializações, veterinários e geração de relatórios."
)
public class GerenteConsultasClinicaController {

    private final GerenteConsultasClinicaServiceImpl gerenteConsultasClinicaService;

    @GetMapping
    @Operation(
            summary = "Listar consultas clínicas",
            description = "Retorna o histórico de consultas clínicas registradas no sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Consultas clínicas encontradas e retornadas com sucesso."
    )
    public ResponseEntity<List<ConsultaClinicaDto>> listarConsultasClinica() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarHistoricoConsultas()
        );
    }

    @GetMapping("/tipos-consulta")
    @Operation(
            summary = "Listar tipos de consulta",
            description = "Retorna todos os tipos de consultas clínicas cadastrados no sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tipos de consulta encontrados e retornados com sucesso."
    )
    public ResponseEntity<List<TiposConsultaDto>> listarTiposConsultas() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarTiposConsulta()
        );
    }

    @GetMapping("/especializacoes")
    @Operation(
            summary = "Listar especializações",
            description = "Retorna todas as especializações cadastradas no sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Especializações encontradas e retornadas com sucesso."
    )
    public ResponseEntity<List<EspecializacoesDto>> listarEspecializacoes() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarEspecializacoes()
        );
    }

    @PostMapping("/especializacoes")
    @Operation(
            summary = "Cadastrar especialização",
            description = "Cria uma nova especialização para utilização no cadastro e gerenciamento "
                    + "de veterinários e tipos de consultas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Especialização criada com sucesso."
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou não passaram pelas validações."
    )
    public ResponseEntity<Void> adicionarNovaEspecializacao(
            @RequestBody @Valid EspecializacaoForm form
    ) {
        gerenteConsultasClinicaService.adicionarNovaEspecializacao(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/especializacoes/{idEspecializacao}")
    @Operation(
            summary = "Consultar detalhes da especialização",
            description = "Retorna os dados detalhados de uma especialização a partir do seu identificador."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Detalhes da especialização encontrados com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Especialização não encontrada para o identificador informado."
    )
    public ResponseEntity<DetalhesEspecialziacaoDto> buscarDetalhesEspecializacaoPorId(
            @Parameter(
                    description = "Identificador da especialização.",
                    example = "1"
            )
            @PathVariable Long idEspecializacao
    ) {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.buscarDetalhesEspecializacoes(idEspecializacao)
        );
    }

    @GetMapping("/tipos-consulta-filtro")
    @Operation(
            summary = "Listar tipos de consulta para filtros",
            description = "Retorna os tipos de consulta em formato apropriado para utilização "
                    + "nos filtros da aplicação."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tipos de consulta retornados com sucesso."
    )
    public ResponseEntity<List<TipoConsultaFiltrosDto>> listarTiposConsultasFiltros() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarTiposConsultasParaFiltros()
        );
    }

    @GetMapping("/clientes")
    @Operation(
            summary = "Listar clientes para filtros",
            description = "Retorna os clientes que podem ser utilizados como filtros "
                    + "na consulta do histórico de atendimentos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Clientes retornados com sucesso."
    )
    public ResponseEntity<List<ParticipanteFiltrosDto>> listarClientesSolicitantesFiltros() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarSolicitantesParaFiltros()
        );
    }

    @GetMapping("/veterinarios")
    @Operation(
            summary = "Listar veterinários para filtros",
            description = "Retorna os veterinários que podem ser utilizados como filtros "
                    + "na consulta do histórico de atendimentos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veterinários retornados com sucesso."
    )
    public ResponseEntity<List<ParticipanteFiltrosDto>> listarClientesVeterinariosFiltros() {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarVeterinariosParaFiltros()
        );
    }

    @GetMapping("/detalhes-consulta/{idConsulta}")
    @Operation(
            summary = "Consultar detalhes da consulta",
            description = "Retorna os dados detalhados de uma consulta clínica "
                    + "a partir do seu identificador."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Detalhes da consulta encontrados com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Consulta não encontrada para o identificador informado."
    )
    public ResponseEntity<DetalhesConsultaDto> buscarDetalhesConsultaPorId(
            @Parameter(
                    description = "Identificador da consulta.",
                    example = "1"
            )
            @PathVariable Long idConsulta
    ) {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.buscarDetalhesConsulta(idConsulta)
        );
    }

    @GetMapping("/buscar-detalhes-tipo-consulta/{idTipoConsulta}")
    @Operation(
            summary = "Consultar detalhes do tipo de consulta",
            description = "Retorna os dados detalhados de um tipo de consulta "
                    + "a partir do seu identificador."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Detalhes do tipo de consulta encontrados com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tipo de consulta não encontrado para o identificador informado."
    )
    public ResponseEntity<DetalhesTipoConsultaDto> buscarDetalhesTipoConsulta(
            @Parameter(
                    description = "Identificador do tipo de consulta.",
                    example = "1"
            )
            @PathVariable Long idTipoConsulta
    ) {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.buscarDetalhesTipoConsulta(idTipoConsulta)
        );
    }

    @PutMapping("/adicionar-tipo-consulta")
    @Operation(
            summary = "Cadastrar tipo de consulta",
            description = "Cadastra um novo tipo de consulta clínica no sistema. "
                    + "O usuário responsável pela operação é obtido a partir do token de autenticação."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tipo de consulta cadastrado com sucesso."
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou não passaram pelas validações."
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado."
    )
    public ResponseEntity<Void> adicionarTipoConsulta(
            HttpServletRequest request,
            @RequestBody @Valid TipoConsultaForm form
    ) {
        TokenModel token = new TokenModel(
                request.getHeader("Authorization")
        );

        gerenteConsultasClinicaService.adicionarNovoTipoConsulta(
                token.getIdUsuario(),
                form
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/editar-informacoes-tipo-consulta/{idTipoConsulta}")
    @Operation(
            summary = "Editar tipo de consulta",
            description = "Atualiza as informações de um tipo de consulta existente. "
                    + "O usuário responsável pela alteração é obtido a partir do token de autenticação."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tipo de consulta atualizado com sucesso."
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou não passaram pelas validações."
    )
    @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tipo de consulta não encontrado."
    )
    public ResponseEntity<Void> editarTipoConsulta(
            HttpServletRequest request,

            @Parameter(
                    description = "Identificador do tipo de consulta.",
                    example = "1"
            )
            @PathVariable Long idTipoConsulta,

            @RequestBody @Valid TipoConsultaForm form
    ) {
        TokenModel token = new TokenModel(
                request.getHeader("Authorization")
        );

        gerenteConsultasClinicaService.editarInformacoesTipoConsulta(
                token.getIdUsuario(),
                form,
                idTipoConsulta
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar-veterinarios-adicionar/{idTipoConsulta}")
    @Operation(
            summary = "Listar veterinários disponíveis para o tipo de consulta",
            description = "Retorna os veterinários que podem ser associados ao tipo de consulta informado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veterinários encontrados e retornados com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tipo de consulta não encontrado."
    )
    public ResponseEntity<List<VeterinarioEspecializacoesDto>> buscarVeterinariosAdicionar(
            @Parameter(
                    description = "Identificador do tipo de consulta.",
                    example = "1"
            )
            @PathVariable Long idTipoConsulta
    ) {
        return ResponseEntity.ok(
                gerenteConsultasClinicaService.listarVeterinariosTipoConsulta(
                        idTipoConsulta
                )
        );
    }

    @PutMapping("/adicionar-veterinario-tipo-consulta/{idVeterinario}/{idTipoConsulta}")
    @Operation(
            summary = "Associar veterinário a tipo de consulta",
            description = "Cria a associação entre um veterinário e um tipo de consulta."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veterinário associado ao tipo de consulta com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Veterinário ou tipo de consulta não encontrado."
    )
    public ResponseEntity<Void> adicionarVeterinarioTipoConsulta(
            @Parameter(
                    description = "Identificador do veterinário.",
                    example = "1"
            )
            @PathVariable Long idVeterinario,

            @Parameter(
                    description = "Identificador do tipo de consulta.",
                    example = "1"
            )
            @PathVariable Long idTipoConsulta
    ) {
        gerenteConsultasClinicaService.adicionarNovoVeterinarioTipoConsulta(
                idVeterinario,
                idTipoConsulta
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remover-veterinario-tipo-consulta/{idVeterinario}/{idTipoConsulta}")
    @Operation(
            summary = "Remover veterinário de tipo de consulta",
            description = "Remove a associação entre um veterinário e um tipo de consulta."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Associação removida com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Veterinário ou tipo de consulta não encontrado."
    )
    public ResponseEntity<Void> removerVeterinarioTipoConsulta(
            @Parameter(
                    description = "Identificador do veterinário.",
                    example = "1"
            )
            @PathVariable Long idVeterinario,

            @Parameter(
                    description = "Identificador do tipo de consulta.",
                    example = "1"
            )
            @PathVariable Long idTipoConsulta
    ) {
        gerenteConsultasClinicaService.removerNovoVeterinarioTipoConsulta(
                idVeterinario,
                idTipoConsulta
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/relatorio")
    @Operation(
            summary = "Gerar relatório de consultas",
            description = "Gera um relatório em formato PDF contendo as consultas "
                    + "de acordo com os filtros informados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Relatório gerado com sucesso."
    )
    @ApiResponse(
            responseCode = "400",
            description = "Filtros inválidos ou não passaram pelas validações."
    )
    public ResponseEntity<byte[]> gerarRelatorioConsultas(
            @RequestBody @Valid FiltroConsultaForm form
    ) {
        byte[] pdf = gerenteConsultasClinicaService.gerarRelatorioConsultas(form);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=RelatorioGenerico.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PutMapping("/especializacoes/{idEspecializacao}/{idVeterinario}")
    @Operation(
            summary = "Associar veterinário a especialização",
            description = "Cria a associação entre um veterinário e uma especialização."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veterinário associado à especialização com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Especialização ou veterinário não encontrado."
    )
    public ResponseEntity<Void> relacionarNovoVeterinarioEspecializacao(
            @Parameter(
                    description = "Identificador da especialização.",
                    example = "1"
            )
            @PathVariable Long idEspecializacao,

            @Parameter(
                    description = "Identificador do veterinário.",
                    example = "1"
            )
            @PathVariable Long idVeterinario
    ) {
        gerenteConsultasClinicaService.adicionarNovoVeterinarioEspecializacao(
                idEspecializacao,
                idVeterinario
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/especializacoes/{idEspecializacao}/{idVeterinario}")
    @Operation(
            summary = "Remover veterinário de especialização",
            description = "Remove a associação entre um veterinário e uma especialização."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veterinário removido da especialização com sucesso."
    )
    @ApiResponse(
            responseCode = "404",
            description = "Especialização ou veterinário não encontrado."
    )
    public ResponseEntity<Void> removerVeterinarioEspecializacao(
            @Parameter(
                    description = "Identificador da especialização.",
                    example = "1"
            )
            @PathVariable Long idEspecializacao,

            @Parameter(
                    description = "Identificador do veterinário.",
                    example = "1"
            )
            @PathVariable Long idVeterinario
    ) {
        gerenteConsultasClinicaService.removerVeterinarioEspecializacao(
                idEspecializacao,
                idVeterinario
        );

        return ResponseEntity.ok().build();
    }
}