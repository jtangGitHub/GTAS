package gov.gtas.controller;

import gov.gtas.model.User;
import gov.gtas.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@Controller
public class LoginController extends AbstractController{
	
	@Autowired
	private UserService userService;
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/login")
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("Testing logging from slf4j from LoginController starting");
		String viewString  = "login";//redirect to login page
		String message="";
		ModelAndView mav = new ModelAndView();
		
		String userName = request.getParameter("username");
		String passWord = request.getParameter("password");
		mav.getModel().put("uname",userName );
		
		if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)){
			message="User name and Password can not be null.Please enter valid credentials to login to GTAS";
			mav.getModel().put("message",message );
			mav.setViewName(viewString);
			return mav;
		}
		User user=userService.findById(userName);
		if(user == null ){
			logger.info("Invalid Credentials");
			message="Invalid User Name.";
			mav.getModel().put("message",message );
			mav.setViewName(viewString);
			return mav;
		}
		else if(user != null && (!user.getPassword().equals(passWord))){
			logger.info("Invalid Password");
			
			message="Invalid Password.";
			mav.getModel().put("message",message );
			mav.setViewName(viewString);
			return mav;
		}
		logger.info("SUCCESS--Navigate to main page");
		mav.addObject("userName",user.getFirstName()+" "+user.getLastName()+" Role: "+user.getUserRole().getRoleDescription());
		viewString="main";
		mav.setViewName(viewString);
		return mav;
	}

}