package br.com.api.petpoints.domain.users.gerente.features.dashboard.services;

import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.AcessosMesDto;
import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.ConsultasDashboardGerenteDto;
import br.com.api.petpoints.domain.users.gerente.features.dashboard.dto.MovimentacoesDashboardGerenteDto;
import br.com.api.petpoints.shared.enums.StatusConsultaEnum;
import br.com.api.petpoints.shared.enums.TipoLogEnum;
import br.com.api.petpoints.shared.models.ConsultaModel;
import br.com.api.petpoints.shared.models.LogsModel;
import br.com.api.petpoints.shared.models.MovimentacaoModel;
import br.com.api.petpoints.shared.repository.ConsultaRepository;
import br.com.api.petpoints.shared.repository.LogsRepository;
import br.com.api.petpoints.shared.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GerenteDashboardServiceImpl implements GerenteDashboardService {

    private final LogsRepository logsRepository;
    private final ConsultaRepository consultaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Override
    public List<AcessosMesDto> buscarAcessosSistema() {
        List<LogsModel> logs = this.logsRepository.findAll().stream().filter(log -> log.getTipo() == TipoLogEnum.LOGIN || log.getTipo() == TipoLogEnum.REGISTRO).toList();
        Map<String, List<LogsModel>> logsAgrupados = logs.stream().collect(Collectors.groupingBy(log -> log.getRegistradoEm().getMonthValue() + "/" + log.getRegistradoEm().getYear()));
        return logsAgrupados.entrySet().stream().map(log -> new AcessosMesDto(log.getKey(), log.getValue().size())).toList();
    }

    @Override
    public List<ConsultasDashboardGerenteDto> buscarConsultasAgendadas() {
        List<ConsultaModel> consultas = this.consultaRepository.findAllByStatus(StatusConsultaEnum.APROVADA).stream().filter(consulta ->
                consulta.getDataConsulta().toLocalDate().equals(LocalDate.now())).toList();
        return ConsultasDashboardGerenteDto.convert(consultas);
    }

    @Override
    public List<MovimentacoesDashboardGerenteDto> buscarMovimentacoesDia() {
        List<MovimentacaoModel> movimentacoes = this.movimentacaoRepository.findAll().stream().filter(consulta ->
                consulta.getMovimentadoEm().toLocalDate().equals(LocalDate.now())).toList();
        return MovimentacoesDashboardGerenteDto.convert(movimentacoes);
    }
}
