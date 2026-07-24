package br.com.api.petpoints.domain.users.gerente.features.financeiro.service;

import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.CardsFinanceiroDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.FaturaDto;
import br.com.api.petpoints.domain.users.gerente.features.financeiro.dto.GraficoReceitaDto;
import br.com.api.petpoints.shared.enums.StatusPagamentoEnum;
import br.com.api.petpoints.shared.models.PagamentoModel;
import br.com.api.petpoints.shared.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class DashboardFinanceiroServiceImpl implements DashboardFinanceiroService {

    private static final String[] MESES = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    private final PagamentoRepository pagamentoRepository;

    @Override
    public CardsFinanceiroDto buscarCards() {
        List<PagamentoModel> pagamentos = this.pagamentoRepository.findAll().stream()
                .filter(pagamento -> pagamento.getDataPagamento() != null)
                .toList();

        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        YearMonth mesAtual = YearMonth.from(hoje);
        YearMonth mesAnterior = mesAtual.minusMonths(1);

        double receitaHoje = this.somarAprovados(pagamentos, pagamento -> pagamento.getDataPagamento().toLocalDate().equals(hoje));
        double receitaOntem = this.somarAprovados(pagamentos, pagamento -> pagamento.getDataPagamento().toLocalDate().equals(ontem));

        double receitaMesAtual = this.somarAprovados(pagamentos, pagamento -> YearMonth.from(pagamento.getDataPagamento().toLocalDate()).equals(mesAtual));
        double receitaMesAnterior = this.somarAprovados(pagamentos, pagamento -> YearMonth.from(pagamento.getDataPagamento().toLocalDate()).equals(mesAnterior));

        List<PagamentoModel> pendentes = pagamentos.stream().filter(this::isPendente).toList();
        List<PagamentoModel> emAtraso = pendentes.stream().filter(this::isVencido).toList();
        List<PagamentoModel> aReceber = pendentes.stream().filter(pagamento -> !this.isVencido(pagamento)).toList();

        double valorPendente = aReceber.stream().mapToDouble(PagamentoModel::getValorPagamento).sum();
        double valorEmAtraso = emAtraso.stream().mapToDouble(PagamentoModel::getValorPagamento).sum();

        return new CardsFinanceiroDto(
                receitaMesAtual, this.calcularVariacao(receitaMesAtual, receitaMesAnterior),
                receitaHoje, this.calcularVariacao(receitaHoje, receitaOntem),
                valorPendente, aReceber.size(),
                valorEmAtraso, emAtraso.size()
        );
    }

    @Override
    public GraficoReceitaDto buscarGrafico(String agrupamento) {
        List<PagamentoModel> aprovados = this.pagamentoRepository.findAll().stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
                .filter(pagamento -> pagamento.getDataPagamento() != null)
                .toList();

        if ("MES".equalsIgnoreCase(agrupamento)) return this.buscarGraficoMensal(aprovados);
        return this.buscarGraficoDiario(aprovados);
    }

    @Override
    public List<FaturaDto> listarFaturas() {
        return this.pagamentoRepository.findAll().stream()
                .filter(pagamento -> pagamento.getDataPagamento() != null)
                .sorted(Comparator.comparing(PagamentoModel::getDataPagamento).reversed())
                .map(FaturaDto::new)
                .toList();
    }

    private GraficoReceitaDto buscarGraficoDiario(List<PagamentoModel> aprovados) {
        LocalDate hoje = LocalDate.now();
        List<String> labels = new ArrayList<>();
        List<Double> valores = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            LocalDate dia = hoje.minusDays(i);
            double total = aprovados.stream()
                    .filter(pagamento -> pagamento.getDataPagamento().toLocalDate().equals(dia))
                    .mapToDouble(PagamentoModel::getValorPagamento)
                    .sum();
            labels.add(String.format("%02d/%02d", dia.getDayOfMonth(), dia.getMonthValue()));
            valores.add(total);
        }

        return new GraficoReceitaDto(labels, valores);
    }

    private GraficoReceitaDto buscarGraficoMensal(List<PagamentoModel> aprovados) {
        YearMonth mesAtual = YearMonth.now();
        List<String> labels = new ArrayList<>();
        List<Double> valores = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            YearMonth mes = mesAtual.minusMonths(i);
            double total = aprovados.stream()
                    .filter(pagamento -> YearMonth.from(pagamento.getDataPagamento().toLocalDate()).equals(mes))
                    .mapToDouble(PagamentoModel::getValorPagamento)
                    .sum();
            labels.add(MESES[mes.getMonthValue() - 1] + "/" + String.valueOf(mes.getYear()).substring(2));
            valores.add(total);
        }

        return new GraficoReceitaDto(labels, valores);
    }

    private double somarAprovados(List<PagamentoModel> pagamentos, Predicate<PagamentoModel> filtro) {
        return pagamentos.stream()
                .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamentoEnum.APROVADO)
                .filter(filtro)
                .mapToDouble(PagamentoModel::getValorPagamento)
                .sum();
    }

    private boolean isPendente(PagamentoModel pagamento) {
        return pagamento.getStatusPagamento() == StatusPagamentoEnum.PENDENTE || pagamento.getStatusPagamento() == StatusPagamentoEnum.ENVIADO;
    }

    private boolean isVencido(PagamentoModel pagamento) {
        return pagamento.getDataLimitePagamento() != null && pagamento.getDataLimitePagamento().toLocalDate().isBefore(LocalDate.now());
    }

    private Double calcularVariacao(double atual, double anterior) {
        if (anterior == 0) return null;
        return ((atual - anterior) / anterior) * 100;
    }
}
