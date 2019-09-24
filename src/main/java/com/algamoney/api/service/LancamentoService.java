package com.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired 
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvar(Lancamento lancamento) {
		return salvarLancamento(lancamento);
	}

	private Lancamento salvarLancamento(Lancamento lancamento) {
		validarPessoa(lancamento);
		
		return lancamentoRepository.save(lancamento);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	public void atualizarLancamento(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoEncontrado = pesquisaLancamento(codigo);
		if(!lancamentoEncontrado.getPessoa().equals(lancamento.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoEncontrado, "codigo");
		
		lancamentoRepository.save(lancamentoEncontrado);
	}

	private Lancamento pesquisaLancamento(Long codigo) {
		Lancamento lancamentoPesquisado = lancamentoRepository.findOne(codigo);
		if(lancamentoPesquisado == null) {
			throw new IllegalArgumentException("Lancamento com código "+codigo+" não existe");
		}
		return lancamentoPesquisado;
	}
	
}
