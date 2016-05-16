package com.swisscom.ais.itext;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Leonardo Pistone on 5/12/16.
 */
public class SignaturePaster {

    public static void main(String[] args) throws IOException, DocumentException {
        String externalSignaturePath = args[0];
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        PdfReader reader = new PdfReader(inputFilePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfSignature pdfSignature = new PdfSignature(
                PdfName.ADOBE_PPKLITE,
                PdfName.ADBE_PKCS7_DETACHED
        );

        PdfStamper pdfStamper = PdfStamper.createSignature(reader, byteArrayOutputStream, '\0');
        PdfSignatureAppearance pdfSignatureAppearance = pdfStamper
                .getSignatureAppearance();
        pdfSignature.setReason(null);
        pdfSignature.setLocation(null);
        pdfSignature.setContact(null);
        pdfSignature.setDate(new PdfDate());
        pdfSignatureAppearance.setCryptoDictionary(pdfSignature);

        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, 44002);
        pdfSignatureAppearance.preClose(exc);

        PdfLiteral pdfLiteral = (PdfLiteral) pdfSignature.get(PdfName.CONTENTS);

        byte[] outc = new byte[(pdfLiteral.getPosLength() - 2) / 2];
        Arrays.fill(outc, (byte) 0);

        byte[] externalSignature = Files.readAllBytes(Paths.get (externalSignaturePath));
        System.arraycopy(externalSignature, 0, outc, 0, externalSignature.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));
        pdfSignatureAppearance.close(dic2);

        OutputStream outputStream = new FileOutputStream(outputFilePath);

        byteArrayOutputStream.writeTo(outputStream);

        if (Soap._debugMode) {
            System.out.println("\nOK writing signature to " + outputFilePath);
        }

        byteArrayOutputStream.close();
        outputStream.close();
    }

    public static void fromItextDoc(String[] args) throws IOException,
            DocumentException {

        PdfReader reader = new PdfReader("one.pdf");

        FileOutputStream fout = new FileOutputStream("mydocs1.pdf");

        PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');

        PdfSignatureAppearance sap = stp.getSignatureAppearance();




        PdfSignature pdfSignature = new PdfSignature(
                PdfName.ADOBE_PPKLITE,
                PdfName.ADBE_PKCS7_DETACHED
        );
    }
}
