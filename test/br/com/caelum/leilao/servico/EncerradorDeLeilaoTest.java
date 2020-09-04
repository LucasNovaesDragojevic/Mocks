package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 01, 01);
		
		Leilao leilao1 = new CriadorDeLeilao().para("RTX 3090").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("RTX 3080").naData(antiga).constroi();
		
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();
		
		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("RTX 3090").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("RTX 3080").naData(ontem).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		
		assertEquals(0, encerrador.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
	}
	
	@Test
    public void naoDeveEncerrarLeiloesCasoNaoHajaNenhum() {
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		
		assertEquals(0, encerrador.getTotalEncerrados());
		
	}
	
}
