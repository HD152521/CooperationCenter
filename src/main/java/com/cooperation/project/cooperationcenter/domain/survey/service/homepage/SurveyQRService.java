package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyQRService {
    public Object cerateQR(String url, HttpServletRequest request) throws WriterException, IOException {
        //qr 크기 설정
        int width = 200;
        int height = 200;

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String fullUrl = url.startsWith("http") ? url : baseUrl + url;
        log.info("fullUrl:{}",fullUrl);

        // QR Code - BitMatrix: qr code 정보 생성
        BitMatrix bitMatrix = new MultiFormatWriter().encode(fullUrl, BarcodeFormat.QR_CODE,width,height);

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();){
            MatrixToImageWriter.writeToStream(bitMatrix,"PNG",out);
            return out.toByteArray();
        }
    }
}
