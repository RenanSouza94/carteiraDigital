package br.com.carteiradigital.infrastructure.configuration;

import br.com.carteiradigital.domain.adapters.ContaUseCaseImpl;
import br.com.carteiradigital.domain.adapters.TransacaoUseCaseImpl;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.domain.port.usecase.ContaUseCase;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		//modelMapper.addMappings(MappingToModel.mapPrevisaoGastosToResponse);
		return modelMapper;
	}

	@Bean
	TransacaoUseCase transacaoUseCase(TransacaoRepository transacaoRepository, LogUseCase logUseCase, ContaRepository contaRepository){
		return new TransacaoUseCaseImpl(transacaoRepository, logUseCase, contaRepository);
	}

	@Bean
	ContaUseCase contaUseCase(ContaRepository contaRepository, LogUseCase log){
		return new ContaUseCaseImpl(contaRepository, log);
	}

}
