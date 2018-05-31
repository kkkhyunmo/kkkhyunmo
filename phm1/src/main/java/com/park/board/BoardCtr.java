package com.park.board;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.park.common.FileUtil;
import com.park.common.FileVO;
import com.park.common.SearchVO;

@Controller 
public class BoardCtr {

    @Autowired
    private BoardSvc boardSvc;
    
    /**
     * 리스트.
     */       
    @RequestMapping(value = "/")
    public String index(SearchVO searchVO, ModelMap modelMap) {

        searchVO.pageCalculate( boardSvc.selectBoardCount(searchVO) ); // startRow, endRow

        List<?> listview  = boardSvc.selectBoardList(searchVO);
        
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("searchVO", searchVO);
        
        return "board/BoardList";
    }
    
    
    @RequestMapping(value = "/boardList")
    public String boardList(SearchVO searchVO, ModelMap modelMap) {

        searchVO.pageCalculate( boardSvc.selectBoardCount(searchVO) ); // startRow, endRow

        List<?> listview  = boardSvc.selectBoardList(searchVO);
        
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("searchVO", searchVO);
        
        return "board/BoardList";
    }
    
    /** 
     * 글 쓰기. 
     */
    @RequestMapping(value = "/boardForm")
    public String boardForm(HttpServletRequest request, ModelMap modelMap) {
        String brdno = request.getParameter("brdno");
        if (brdno != null) {
            BoardVO boardInfo = boardSvc.selectBoardOne(brdno);
            List<?> listview = boardSvc.selectBoard6FileList(brdno);
        
            modelMap.addAttribute("boardInfo", boardInfo);
            modelMap.addAttribute("listview", listview);
        }
        
        return "board/BoardForm";
    }
    
    /**
     * 글 저장.
     */
    @RequestMapping(value = "/boardSave")
    public String boardSave(HttpServletRequest request, BoardVO boardInfo) {
        String[] fileno = request.getParameterValues("fileno");
        
        FileUtil fs = new FileUtil();
        List<FileVO> filelist = fs.saveAllFiles(boardInfo.getUploadfile());

        boardSvc.insertBoard(boardInfo, filelist, fileno);

        return "redirect:/boardList";
    }

    /**
     * 글 읽기.
     */
    @RequestMapping(value = "/boardRead")
    public String board6Read(HttpServletRequest request, ModelMap modelMap) {
        
        String brdno = request.getParameter("brdno");
        
        boardSvc.updateBoard6Read(brdno);
        BoardVO boardInfo = boardSvc.selectBoardOne(brdno);
        List<?> listview = boardSvc.selectBoard6FileList(brdno);
        List<?> replylist = boardSvc.selectBoard6ReplyList(brdno);
        
        modelMap.addAttribute("boardInfo", boardInfo);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("replylist", replylist);
        
        return "board/BoardRead";
    }
    
    /**
     * 글 삭제.
     */
    @RequestMapping(value = "/boardDelete")
    public String boardDelete(HttpServletRequest request) {
        
        String brdno = request.getParameter("brdno");
        
        boardSvc.deleteBoardOne(brdno);
        
        return "redirect:/boardList";
    }

    /* ===================================================================== */
    
    /**
     * 댓글 저장.
     */
    @RequestMapping(value = "/boardReplySave")
    public String board6ReplySave(HttpServletRequest request, BoardReplyVO boardReplyInfo) {
        
        boardSvc.insertBoardReply(boardReplyInfo);

        return "redirect:/boardRead?brdno=" + boardReplyInfo.getBrdno();
    }
    
    /**
     * 댓글 삭제.
     */
    @RequestMapping(value = "/boardReplyDelete")
    public String board6ReplyDelete(HttpServletRequest request, BoardReplyVO boardReplyInfo) {
        
        if (!boardSvc.deleteBoard6Reply(boardReplyInfo.getReno()) ) {
            return "board/BoardFailure";
        }

        return "redirect:/boardRead?brdno=" + boardReplyInfo.getBrdno();
    }      
}
