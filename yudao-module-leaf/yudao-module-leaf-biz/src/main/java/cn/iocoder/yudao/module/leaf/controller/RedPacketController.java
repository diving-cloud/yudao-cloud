package cn.iocoder.yudao.module.leaf.controller;


import cn.iocoder.yudao.module.leaf.controller.app.redpacket.vo.GrabRedPacketResponse;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacket;
import cn.iocoder.yudao.module.leaf.dal.dataobject.redpacket.RedPacketRecord;
import cn.iocoder.yudao.module.leaf.dto.redpacket.RedPacketTokenDTO;
import cn.iocoder.yudao.module.leaf.request.RedPacketCreateRequest;
import cn.iocoder.yudao.module.leaf.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/red-packet")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;
    
    @PostMapping
    public ResponseEntity<RedPacket> createRedPacket(@RequestBody RedPacketCreateRequest request) {
        RedPacket createdRedPacket = redPacketService.createRedPacket(request);
        return ResponseEntity.ok(createdRedPacket);
    }
    
    @GetMapping("/{redPacketId}/token")
    public ResponseEntity<RedPacketTokenDTO> getToken(
            @PathVariable Long redPacketId,
            @RequestParam Long userId) {
        RedPacketTokenDTO tokenDTO = redPacketService.generateToken(redPacketId, userId);
        return ResponseEntity.ok(tokenDTO);
    }
    
    @PostMapping("/grab")
    public ResponseEntity<GrabRedPacketResponse> grabRedPacket(@RequestBody RedPacketTokenDTO tokenDTO) {
        BigDecimal amount = redPacketService.grabRedPacket(tokenDTO);
        
        GrabRedPacketResponse response = new GrabRedPacketResponse();
        response.setRedPacketId(tokenDTO.getRedPacketId());
        response.setUserId(tokenDTO.getUserId());
        response.setAmount(amount);
        response.setGrabTime(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{redPacketId}/records")
    public ResponseEntity<List<RedPacketRecord>> getRedPacketRecords(@PathVariable Long redPacketId) {
        List<RedPacketRecord> records = redPacketService.getRedPacketRecords(redPacketId);
        return ResponseEntity.ok(records);
    }
    
    @PutMapping("/{redPacketId}/followers")
    public ResponseEntity<Void> updateFollowers(
            @PathVariable Long redPacketId,
            @RequestParam Integer followers) {
        redPacketService.updateFollowers(redPacketId, followers);
        return ResponseEntity.ok().build();
    }
}