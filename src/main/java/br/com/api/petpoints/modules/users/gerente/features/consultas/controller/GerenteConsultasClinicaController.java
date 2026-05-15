package br.com.api.petpoints.modules.users.gerente.features.consultas.controller;

import br.com.api.petpoints.core.token.TokenModel;
import br.com.api.petpoints.modules.users.gerente.features.clientes.dto.ClienteDto;
import br.com.api.petpoints.modules.users.gerente.features.consultas.dto.*;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.EspecializacaoForm;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.FiltroConsultaForm;
import br.com.api.petpoints.modules.users.gerente.features.consultas.form.TipoConsultaForm;
import br.com.api.petpoints.modules.users.gerente.features.consultas.service.GerenteConsultasClinicaServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente/consultas-clinica")
@RequiredArgsConstructor
public class GerenteConsultasClinicaController {

    private final GerenteConsultasClinicaServiceImpl gerenteConsultasClinicaService;

    @GetMapping
    public ResponseEntity<List<ConsultaClinicaDto>> listarConsultasClinica() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarHistoricoConsultas());
    }

    @GetMapping("/tipos-consulta")
    public ResponseEntity<List<TiposConsultaDto>> listarTiposConsultas() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarTiposConsulta());
    }

    @GetMapping("/especializacoes")
    public ResponseEntity<List<EspecializacoesDto>> listarEspecializacoes() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarEspecializacoes());
    }

    @PostMapping("/especializacoes")
    public ResponseEntity<Void> adicionarNovaEspecializacao(@RequestBody @Valid EspecializacaoForm form) {
        this.gerenteConsultasClinicaService.adicionarNovaEspecializacao(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("especializacoes/{idEspecializacao}")
    public ResponseEntity<DetalhesEspecialziacaoDto> buscarDetalhesEspecializacaoPorId(@PathVariable Long idEspecializacao) {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.buscarDetalhesEspecializacoes(idEspecializacao));
    }

    @GetMapping("/tipos-consulta-filtro")
    public ResponseEntity<List<TipoConsultaFiltrosDto>> listarTiposConsultasFiltros() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarTiposConsultasParaFiltros());
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<ParticipanteFiltrosDto>> listarClientesSolicitantesFiltros() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarSolicitantesParaFiltros());
    }

    @GetMapping("/veterinarios")
    public ResponseEntity<List<ParticipanteFiltrosDto>> listarClientesVeterinariosFiltros() {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarVeterinariosParaFiltros());
    }

    @GetMapping("/detalhes-consulta/{idConsulta}")
    public ResponseEntity<DetalhesConsultaDto> buscarDetalhesConsultaPorId(@PathVariable Long idConsulta) {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.buscarDetalhesConsulta(idConsulta));
    }

    @GetMapping("/buscar-detalhes-tipo-consulta/{idTipoConsulta}")
    public ResponseEntity<DetalhesTipoConsultaDto> buscarDetalhesTipoConsulta(@PathVariable Long idTipoConsulta) {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.buscarDetalhesTipoConsulta(idTipoConsulta));
    }

    @PutMapping("/adicionar-tipo-consulta")
    public ResponseEntity<Void> adicionarTipoConsulta(HttpServletRequest request, @RequestBody @Valid TipoConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.gerenteConsultasClinicaService.adicionarNovoTipoConsulta(token.getIdUsuario(), form);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editar-informacoes-tipo-consulta/{idTipoConsulta}")
    public ResponseEntity<Void> editarTipoConsulta(HttpServletRequest request, @PathVariable Long idTipoConsulta, @RequestBody @Valid TipoConsultaForm form) {
        TokenModel token = new TokenModel(request.getHeader("Authorization"));
        this.gerenteConsultasClinicaService.editarInformacoesTipoConsulta(token.getIdUsuario(), form, idTipoConsulta);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar-veterinarios-adicionar/{idTipoConsulta}")
    public ResponseEntity<List<VeterinarioEspecializacoesDto>> buscarVeterinariosAdicionar(@PathVariable Long idTipoConsulta) {
        return ResponseEntity.ok().body(this.gerenteConsultasClinicaService.listarVeterinariosTipoConsulta(idTipoConsulta));
    }

    @PutMapping("/adicionar-veterinario-tipo-consulta/{idVeterinario}/{idTipoConsulta}")
    public ResponseEntity<Void> adicionarVeterinarioTipoConsulta(@PathVariable Long idTipoConsulta, @PathVariable Long idVeterinario) {
        this.gerenteConsultasClinicaService.adicionarNovoVeterinarioTipoConsulta(idVeterinario, idTipoConsulta);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remover-veterinario-tipo-consulta/{idVeterinario}/{idTipoConsulta}")
    public ResponseEntity<Void> removerVeterinarioTipoConsulta(@PathVariable Long idTipoConsulta, @PathVariable Long idVeterinario) {
        this.gerenteConsultasClinicaService.removerNovoVeterinarioTipoConsulta(idVeterinario, idTipoConsulta);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioConsultas(@Valid @RequestBody FiltroConsultaForm form) {
        byte[] pdf = this.gerenteConsultasClinicaService.gerarRelatorioConsultas(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RelatorioGenerico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PutMapping("/especializacoes/{idEspecializacao}/{idVeterinario}")
    public ResponseEntity<Void> relacionarNovoVeterinarioEspecializacao(@PathVariable Long idEspecializacao, @PathVariable Long idVeterinario){
        this.gerenteConsultasClinicaService.adicionarNovoVeteterinarioEspecializacao(idEspecializacao, idVeterinario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/especializacoes/{idEspecializacao}/{idVeterinario}")
    public ResponseEntity<Void> removerVeterinarioEspecializacao(@PathVariable Long idEspecializacao, @PathVariable Long idVeterinario){
        this.gerenteConsultasClinicaService.removerVeteterinarioEspecializacao(idEspecializacao, idVeterinario);
        return ResponseEntity.ok().build();
    }
}
