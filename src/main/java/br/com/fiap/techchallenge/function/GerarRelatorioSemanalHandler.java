package br.com.fiap.techchallenge.function;

import java.time.LocalDate;
import java.util.List;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

import br.com.fiap.techchallenge.model.Avaliacao;
import br.com.fiap.techchallenge.publisher.AdminPublisher;
import br.com.fiap.techchallenge.repository.AvaliacaoRepository;
import br.com.fiap.techchallenge.service.RelatorioService;
import br.com.fiap.techchallenge.service.RelatorioService.Relatorio;
import br.com.fiap.techchallenge.util.Datas;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named ("gerar-relatorio-semanal")
public class GerarRelatorioSemanalHandler implements RequestHandler<ScheduledEvent, String> {

    private static final Logger LOG = Logger.getLogger(GerarRelatorioSemanalHandler.class);

    @Inject 
    AvaliacaoRepository repository;

    @Inject
    RelatorioService relatorioService;

    @Inject
    AdminPublisher adminPublisher;

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        LocalDate fim = LocalDate.now(Datas.ZONA);
        LocalDate inicio = fim.minusDays(6);
        String desde = inicio.atStartOfDay(Datas.ZONA).toInstant().toString();

        List<Avaliacao> avaliacoes = repository.buscarDesde(desde);
        Relatorio relatorio = relatorioService.gerar(avaliacoes, inicio, fim);

        adminPublisher.enviar(relatorioService.assunto(relatorio), relatorioService.formatarTexto(relatorio));

        LOG.infof("Relatorio semanal enviado: total=%d media=%.2f", relatorio.total(), relatorio.media());
        return "OK";
    }
}
