package co.ke.bigfootke.app.jpa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.ke.bigfootke.app.jpa.entities.Client;
import co.ke.bigfootke.app.jpa.service.ClientJpaService;
import co.ke.bigfootke.app.pojos.Family;

@RestController
@RequestMapping(value = "api/client")
//@CrossOrigin(origins="http://localhost:4200")
public class ClientJpaController {

	@Autowired
	private ClientJpaService service;
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> create(@RequestParam("Country_Code") String countryCode,
										@RequestParam("PhoneNo") String phoneNo,
										@RequestParam("FullName") String fullName,
										@RequestParam("Telecom") String telecom){	
		Client client = new Client();
		client.setCountryCode(countryCode);
		client.setPhoneNo(phoneNo);
		client.setFullName(fullName);
		client.setTelecom(telecom);
		return service.create(client);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Page<Client>> findAll(@PathVariable int pageNo, @PathVariable int pageSize) {
		return service.findAll(pageNo, pageSize);	
	}
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> findById(@RequestParam("clientId") Long clientId) {
		return service.findById(clientId);	
	}
	@RequestMapping(method=RequestMethod.GET, value = "/byphoneNo")
	public ResponseEntity<Object> getByPhone(@RequestParam("phoneNo") String phoneNo) {
		return service.findByPhone(phoneNo);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/group/{groupId}/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Object> findByGroup(@PathVariable Long groupId, @PathVariable int pageNo, 
													@PathVariable int pageSize) {
		return service.findByGroup(groupId, pageNo, pageSize);	
	}
	/**Add Multiple Clients from Excel 2007
	 * by database batch processing **/
	@RequestMapping(value = "/batchCreate", method = RequestMethod.POST)
	public ResponseEntity<Object> createClientsFromExcel(@RequestParam("file") MultipartFile excelfile){	
		return service.batchProcessClients(excelfile);	
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Object> update(@RequestBody Client client) {
		return service.update(client);
	}
	
	@RequestMapping(method=RequestMethod.DELETE)	
	public ResponseEntity<Object> delete(@RequestParam("clientId") Long clientId){
		return service.delete(clientId);
	}
	/** Add Client(s) to group **/
	@RequestMapping(method=RequestMethod.POST, value="/group")
	public ResponseEntity<Object> addToGroup(@RequestBody Family family) {		
		return service.addToGroup(family.getParentId(), family.getChildrenIds());
	}
	@RequestMapping(method=RequestMethod.DELETE, value = "/delete/from/{groupId}")	
	public ResponseEntity<Object> deleteFromGroup(@RequestParam("clientId") Long clientId, @PathVariable Long groupId){
		return service.deleteFromGroup(clientId, groupId);
	}
}
