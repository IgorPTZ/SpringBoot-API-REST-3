package api.rest.three.service;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public byte[] gerarRelatorio(String nomeDoRelatorio,  ServletContext servletContext) {
		
		try {
			
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
			byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			connection.close();
			
			return pdf;
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	public byte[] gerarRelatorioParametrizado(String nomeDoRelatorio,
			                                  Map<String, Object> parametros,
			                                  ServletContext servletContext) {
		
		try {
			
			/* Obtem a conexao com o banco de dados */
			Connection connection = jdbcTemplate.getDataSource().getConnection();
			
			/* Carrega o caminho do arquivo Jasper */
			String caminhoDoArquivoJasper = servletContext.getRealPath("relatorios") +
					                        File.separator + nomeDoRelatorio + ".jasper";
			
			/* Gera o relatorio com os dados */
			JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoDoArquivoJasper,
					                                               parametros,
					                                               connection);
			
			/* Exporta o PDF em forma de bytes, para que seja possivel fazer o download */
			byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			connection.close();
			
			return pdf;
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
}
