package co.ke.bigfootke.app.jpa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.ke.bigfootke.app.jpa.entities.Client;
import co.ke.bigfootke.app.jpa.implementations.ClientJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.GroupJpaImplementation;

@Service
public class ClientJpaService {

	@Autowired
	private ClientJpaImplementation repository;
	@Autowired
	private GroupJpaImplementation groupImpl;
	private Map<String, String> response;
	
	public ResponseEntity<Object> create(Client client) {
		response = new HashMap<>();
		if(client.getPhoneNo() == null || repository.findByPhone(client.getPhoneNo()) != null) {
			response.put("message", "Error: Phone No may already exist or phone No not included");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
		Client newClient = repository.create(client);
		response.put("message", "Success: Created "+newClient);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	public ResponseEntity<Object> findById(Long clientId) {
		if(!repository.exists(clientId)) {
			response = new HashMap<>();
			response.put("message", "Error: Client not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(repository.findById(clientId), HttpStatus.OK);
	}
	
	@Cacheable(value = "clients.paged", key="#pageNo", unless = "#result != null")
	public ResponseEntity<Page<Client>> findAll(int pageNo, int pageSize) {
		return repository.findAll(pageNo, pageSize);
	} 
	
	@Cacheable(value = "client.byGroup", key="#groupId", unless = "#result != null")
	public ResponseEntity<Object> findByGroup(Long groupId, int pageNo, int pageSize) {
		if(!groupImpl.exists(groupId)) {
			response.put("message", "Error: Could not find group");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);				
		}
		final Page<Client> clients = repository.findByGroup(groupId, pageNo, pageSize);
		return new ResponseEntity<Object>(clients, HttpStatus.OK);
	} 
	
	public ResponseEntity<Object> batchProcessClients(MultipartFile excelfile) {
		List<Client> rawClients = new ArrayList<>();
		Map<String, String> response = new HashMap<>();
		
		try {			
			int i = 0;
			// Creates a workbook object from the uploaded excelfile
			XSSFWorkbook workbook = new XSSFWorkbook(excelfile.getInputStream());
			// Creates a worksheet object representing the first sheet
			XSSFSheet worksheet = workbook.getSheetAt(0);
			// Reads the data in excel file until last row is encountered
			while (i <= worksheet.getLastRowNum()) {
				// Creates an object representing a single row in excel
				XSSFRow row = worksheet.getRow(i++);
				// Creates an object for the Client Model
				Client client = new Client();
				// Sets the Read data to the model class				
				client.setCountryCode("+"+(Integer.toString((int) row.getCell(0).getNumericCellValue())));
				client.setPhoneNo(Integer.toString((int) row.getCell(1).getNumericCellValue()));
				client.setFullName(row.getCell(2).getStringCellValue());
				client.setTelecom(row.getCell(3).getStringCellValue());				
				//Add client to the rawClients List
				rawClients.add(client);
			}			
			workbook.close();
			//Check if clients already exist and get the clients that do not exist
			List<Client> newClients = repository.batchCheckIfClientExist(rawClients);
			//save the clients that do not exist to database
			repository.batchCreateNewClients(newClients);
			response.put("message", "Successfully CREATED "+newClients.size()+" NEW Clients. If more expected"
					+ " they were duplicates! ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//should return message of completion 
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Cacheable(value = "client.byPhone", key="#phoneNo", unless = "#result != null")
	public ResponseEntity<Object> findByPhone(String phoneNo) {
		final Client client = repository.findByPhone(phoneNo);
		if(client == null) {
			response = new HashMap<>();
			response.put("message", "Error: Could not find "+phoneNo);
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(client, HttpStatus.OK);
	}	
	
	@Caching(evict = {@CacheEvict(value = "client.byPhone", allEntries = true)},
        	put = {@CachePut(value = "clients.paged", key = "#client.clientId", unless = "#result != null"),
        			@CachePut(value = "client.byGroup", key = "#client.clientId", unless = "#result != null")})
	public ResponseEntity<Object> update(Client client) {
		response = new HashMap<>();
		if(!repository.exists(client.getClientId())) {
			response.put("message", "Error: Could not find client");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(repository.update(client), HttpStatus.OK);			
	}
	
	@Caching(evict = {@CacheEvict(value = "clients.paged", key = "#clientId"), 
			@CacheEvict(value = "client.byPhone", key = "#clientId"),
			@CacheEvict(value = "client.byGroup", key = "#clientId")})
	public ResponseEntity<Object> delete(Long clientId) {
		if(!repository.exists(clientId)) {
			response = new HashMap<>();
			response.put("message", "Error: Client not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);			
		}
		response.put("message", "Deleted");
		repository.delete(clientId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);	
	}

	public ResponseEntity<Object> addToGroup(Long parentId, List<Long> childrenIds) {
		return repository.addToGroup(parentId, childrenIds);
	}	

	public ResponseEntity<Object> deleteFromGroup(Long clientId, Long groupId) {
		response = new HashMap<>();
		if(!repository.exists(clientId) || clientId <= 0L) {
			response.put("message", "Error: Could not find client");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);			
		}else if(!groupImpl.exists(groupId)) {
			response.put("message", "Error: Could not find group");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);			
		}
		response.put("message", "Deleted");
		repository.deleteFromGroup(clientId, groupId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);	
	}	
}
