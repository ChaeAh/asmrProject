package com.asmr.app;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asmr.model.AsmrVoted;
import com.asmr.model.CommentDTO;
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
	public String soundsave(@RequestParam("sound")String sound,@RequestParam("memo")String memo,HttpSession session) {
		sound = sound.substring(0,sound.length()-1);
		System.out.println("��Ʈ�ѷ� ���� :" + sound);
		String username = (String) session.getAttribute("id");
		SaveDTO save = new SaveDTO();
		save.setSound(sound);
		save.setUsername(username);
		save.setMemo(memo);
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
	/* ���ǹ迭�ҷ�����(�������� �־) */
	@GetMapping("loadColabo")
	@ResponseBody
	public String loadColabo(@RequestParam("username")String username) {
		SaveDTO loaded = mService.soundLoad(username);
		if(loaded==null) {
			return "����� ���� �����ϴ�";
		}
		else {
			return loaded.getSound();
		}
	}
	/* ���â �ҷ����� */
	//produce �κ��� ��� json���·� �� �����Ͱ� �ѱ�ȭ�ȴ�. ���ͷδ� ������ �ȵ�
	@RequestMapping(value="C_List",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String C_List() {
		List<CommentDTO> arr = mService.commentList();
		JSONArray jarr = new JSONArray();
		for(CommentDTO cb : arr){
			JSONObject obj = new JSONObject();
			obj.put("num",cb.getNum());
			obj.put("msg",cb.getMsg());
			obj.put("writer",cb.getWriter());
			obj.put("regdate",cb.getRegdate());
			jarr.add(obj);
		}
		return jarr.toString();
	}
	
	/* ��۾��� */
	@RequestMapping(value="C_Insert",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String C_Insert(String msg,String writer) {
		CommentDTO cd = new CommentDTO();
		cd.setWriter(writer);
		cd.setMsg(msg);
		mService.commentInsert(cd);
		List<CommentDTO> arr = mService.commentList();
		JSONArray jarr = new JSONArray();
		for(CommentDTO cb : arr){
			JSONObject obj = new JSONObject();
			obj.put("num",cb.getNum());
			obj.put("msg",cb.getMsg());
			obj.put("writer",cb.getWriter());
			obj.put("regdate",cb.getRegdate());
			jarr.add(obj);
		}
		return jarr.toString();
	}
	
	
	/* �ݶ� �ҷ����� */
	//produce �κ��� ��� json���·� �� �����Ͱ� �ѱ�ȭ�ȴ�. ���ͷδ� ������ �ȵ�
	@RequestMapping(value="colaboList",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String colaboList() {
		List<SaveDTO> arr = mService.colaboList();
		JSONArray jarr = new JSONArray();
		for(SaveDTO cb : arr){
			JSONObject obj = new JSONObject();
			obj.put("num",cb.getNum());
			obj.put("username",cb.getUsername());
			obj.put("memo",cb.getMemo());
			obj.put("voted",cb.getVoted());
			jarr.add(obj);
		}
		return jarr.toString();
	}
	
	/* ��õ�� �ø��� */
	@GetMapping("votedUp")
	@ResponseBody
	@Transactional
	public String votedUp(@RequestParam("num")String num,HttpSession session) {
		String voteuser = (String) session.getAttribute("id");
		mService.votedUp(num);
		
		
		AsmrVoted vote = new AsmrVoted();
		vote.setColabonum(Integer.parseInt(num));
		vote.setVoteuser(voteuser);
		mService.votedSave(vote);
		return "1";
	}
	/* ��õ�� ������ */
	@GetMapping("votedDown")
	@ResponseBody
	@Transactional
	public String votedDown(@RequestParam("num")String num,HttpSession session) {
		String voteuser = (String) session.getAttribute("id");
		mService.votedDown(num);
		
		
		AsmrVoted vote = new AsmrVoted();
		vote.setColabonum(Integer.parseInt(num));
		vote.setVoteuser(voteuser);
		mService.votedDelete(vote);
		return "1";
	}
	
	/* ��õ�� �� �ִ��� ��ȸ�ϱ� */
	@GetMapping("votedCheck")
	@ResponseBody
	public String votedCheck(@RequestParam("colabonum")String colabonum,HttpSession session) {
		String voteuser = (String) session.getAttribute("id");
		System.out.println("�ݶ󺸳ѹ� "+colabonum + "�̰� ���̵�� "+voteuser);
		
		AsmrVoted vote = new AsmrVoted();
		vote.setColabonum(Integer.parseInt(colabonum));
		vote.setVoteuser(voteuser);
		System.out.println("���ϵǴ� �� : "+mService.votedCheck(vote).trim());
		return mService.votedCheck(vote).trim();
	}

}
