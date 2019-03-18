package com.asmr.app;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asmr.model.SaveDTO;
import com.asmr.model.UserDTO;
import com.asmr.service.AsmrService;



/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Inject
	private AsmrService mService;
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);	
		return "home";
	}
	
	/* ȸ������â */
	/* ȸ������ */
	@PostMapping("memberInsert")
	@ResponseBody
	public void memberInsert(UserDTO user) {
		System.out.println(user.getId() +" - "+ user.getName() +" - "+ user.getPassword() +" - "+ user.getPhone());
		mService.userInsert(user);
	}
	/* ȸ�����Խ� �ߺ�üũ */
	@GetMapping("idCheck")
	/* responseBody�� �Ⱦ��� ok.jsp�� ã�ư��� */
	@ResponseBody
	public String idCheck(@RequestParam("userid")String userid) {
		return mService.idCheck(userid).trim();
	}

	/* �α��� */
	@PostMapping("login")
	@ResponseBody
	public String login(UserDTO user, HttpSession session) {
		System.out.println(user.getId() +" - "+ user.getPassword());
		String str = mService.login(user).trim();
		if(str.equals("ok")) {
		
			session.setAttribute("id", user.getId());
			//System.out.println(session.getAttribute("id")+"");
		}
		return str;
	}
	/* �α׾ƿ� */
	@GetMapping("logout")
	public String logout(HttpSession session) {
		
		//System.out.println(session.getAttribute("id")+"");
		session.removeAttribute("id");
		return "home";
	}
	
	/* ���ǹ迭���� */
	@GetMapping("sounds")
	@ResponseBody
	public String soundsave(@RequestParam("sound")String sound,HttpSession session) {
		sound = sound.substring(0,sound.length()-1);
		System.out.println("��Ʈ�ѷ� ���� :" + sound);
		String username = (String) session.getAttribute("id");
		SaveDTO save = new SaveDTO();
		save.setSound(sound);
		save.setUsername(username);
		mService.soundsave(save);
		return "1";
	}
	
	/* ���ǹ迭�ҷ����� */
	@GetMapping("load")
	@ResponseBody
	public String soundLoad(HttpSession session) {
		String username = (String) session.getAttribute("id");
		SaveDTO loaded = mService.soundLoad(username);
		if(loaded==null) {
			return "����� ���� �����ϴ�";
		}
		else {
			return loaded.getSound();
		}
	}

}
