package com.algamoney.api.repository.pessoas;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.algamoney.api.model.Pessoa;
import com.algamoney.api.model.Pessoa_;
import com.algamoney.api.repository.filter.PessoaFilter;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	

	private Predicate[] criarRestricoes(PessoaFilter pessoaFilter, CriteriaBuilder builder,
			Root<Pessoa> root) {
		List<Predicate> predicates = new ArrayList<>();

		if (!StringUtils.isEmpty(pessoaFilter.getNome())) {
			predicates.add(builder.like(builder.lower(root.get(Pessoa_.nome)),
					"%" + pessoaFilter.getNome().toLowerCase() + "%"));
		}
		
		if (pessoaFilter.getAtivo() != null) {
			predicates.add(builder.equal(root.get(Pessoa_.ativo),pessoaFilter.getAtivo()));
		}

//		private String logradouro;
//		private String numero;
//		private String complemento;
//		private String bairro;
//		private String cep;
//		private String cidade;
//		private String estado;
//		private Boolean ativo;

		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	private Long total(PessoaFilter pessoasFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);

		Predicate[] predicates = criarRestricoes(pessoasFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

	@Override
	public Page<Pessoa> filtrar(PessoaFilter pessoasFilter, Pageable pageable) {
		

			CriteriaBuilder builder = manager.getCriteriaBuilder();
			CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
			Root<Pessoa> root = criteria.from(Pessoa.class);

			Predicate[] predicates = criarRestricoes(pessoasFilter, builder, root);
			criteria.where(predicates);

			TypedQuery<Pessoa> query = manager.createQuery(criteria);
			adicionarRestricoesDePaginacao(query, pageable);

			return new PageImpl<>(query.getResultList(), pageable, total(pessoasFilter));
		
	}

}
