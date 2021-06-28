package api.rest.three.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService {
	
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unchecked")
	public byte[] gerarRelatorio(String nomeDoRelatorio,  ServletContext servletContext) throws Exception{

			/* Obtem a conexao com o banco de dados */
			Connection connection = jdbcTemplate.getDataSource().getConnection();
			
			/* Carrega o caminho do arquivo Jasper */
			String caminhoDoArquivoJasper = servletContext.getRealPath("relatorios") + 
					                        File.separator + nomeDoRelatorio + ".jasper";
			
			/* Gera o relatorio com os dados */
			JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoDoArquivoJasper, 
					                                                     new HashMap(),
					                                                     connection);
			
			/* Exporta o PDF em forma de bytes, para que seja possivel fazer o download */
			
			return null;
	}
}
