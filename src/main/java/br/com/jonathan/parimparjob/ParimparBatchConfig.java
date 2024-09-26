package br.com.jonathan.parimparjob;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ParimparBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ParimparBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job imprimeParImparJob(Step imprimirParImparStep){
        return new JobBuilder("imprimeParImparJob", jobRepository).start(imprimirParImparStep).incrementer(new RunIdIncrementer()).build();
    }

    @Bean
    public Step imprimirParImparStep(IteratorItemReader<Integer> contaAteDezReader, FunctionItemProcessor<Integer, String> parOuImparProcessor, ItemWriter<String> imprimeWriter){
        return new StepBuilder("imprimeParImparStep", jobRepository).<Integer, String>chunk(1, transactionManager)
        .reader(contaAteDezReader).processor(parOuImparProcessor).writer(imprimeWriter).build();
    }

    @Bean
    public IteratorItemReader<Integer> contaAteDezReader(){
        List<Integer> numeroDeUmAteDez = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        return new IteratorItemReader<Integer>(numeroDeUmAteDez.iterator());
    }

    @Bean
    public FunctionItemProcessor<Integer, String> parOuImparProcessor(){
        return new FunctionItemProcessor<Integer, String>(item -> item % 2 == 0 ? String.format("Item %s é par", item) : String.format("Item %s é impar", item));
    }

    @Bean
    public ItemWriter<String> imprimeWriter(){
        return itens -> itens.forEach(System.out::println);
    }

}
