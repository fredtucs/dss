package eu.europa.esig.dss.pdf.openpdf.visible;

import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfTemplate;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.pades.SignatureImageTextParameters.SignerPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class TextAndImageSignatureDrawer extends AbstractITextSignatureDrawer {

    private static final Logger LOG = LoggerFactory.getLogger(TextAndImageSignatureDrawer.class);

    @Override
    public void draw() throws IOException {

        final Image image = Image.getInstance(DSSUtils.toByteArray(parameters.getImage()));
        final String text = parameters.getTextParameters().getText();

        float x1 = parameters.getxAxis();
        float y1 = parameters.getyAxis();
        float width = parameters.getWidth();
        float height = parameters.getHeight();
        final Font font = parameters.getTextParameters().getFont();
        final Rectangle pageSize = appearance.getStamper().getReader().getPageSize(parameters.getPage());
        float originY = pageSize.getHeight();
        final Rectangle rectangle = new Rectangle(x1, originY - y1 - height, x1 + width, originY - y1);
        final SignerPosition signerNamePosition = parameters.getTextParameters().getSignerNamePosition();

        appearance.setAcro6Layers(true);
        appearance.setVisibleSignature(rectangle, parameters.getPage());

        PdfTemplate layer = appearance.getLayer(2);

        com.lowagie.text.Font iFont = new com.lowagie.text.Font();
        iFont.setFamily(font.getFamily());
        iFont.setSize(font.getSize() / 1.5f);
        iFont.setStyle(font.getStyle());

        Paragraph paragraph;
        ColumnText columnText, columnTextImage;
        float imgAreaWidth;

        switch (signerNamePosition) {
            case LEFT:
                float imgWidth = (width * 30.0f) / 100.0f;
                imgAreaWidth = imgWidth + 2;
                appearance.setRender(PdfSignatureAppearance.SignatureRenderGraphicAndDescription);
                paragraph = new Paragraph(text, iFont);
                paragraph.setLeading(0, 1.17f);
                paragraph.setAlignment(Paragraph.ALIGN_LEFT);

                columnTextImage = new ColumnText(layer);
                columnTextImage.addElement(image);
                columnTextImage.setSimpleColumn(0, 0, imgWidth, height);
                columnTextImage.go();
                columnText = new ColumnText(layer);
                columnText.setSimpleColumn(imgAreaWidth, 0, width, height);
                columnText.addElement(paragraph);
                columnText.go();
                break;
            case BOTTOM:
                appearance.setRender(PdfSignatureAppearance.SignatureRenderGraphicAndDescription);

                float imgHeight = (height * 0.30f);
                float diff = image.getHeight() - image.getWidth();
                imgWidth = (imgHeight - diff);
                float pad = (width - imgWidth) / 2;

                columnTextImage = new ColumnText(layer);
                columnTextImage.addElement(image);
                columnTextImage.setSimpleColumn(pad, rectangle.getHeight(), imgWidth + pad, imgHeight);
                columnTextImage.go();

                paragraph = new Paragraph(text, iFont);
                paragraph.setLeading(0, 1.17f);
                paragraph.setAlignment(Paragraph.ALIGN_CENTER);

                columnText = new ColumnText(layer);
                columnText.setSimpleColumn(0, 0, width, columnTextImage.getYLine());
                columnText.addElement(paragraph);
                columnText.go();
                break;
            default:
                appearance.setRender(PdfSignatureAppearance.SignatureRenderDescription);
                paragraph = new Paragraph(text, iFont);
                paragraph.setLeading(0, 1.17f);
                paragraph.setAlignment(Paragraph.ALIGN_CENTER);

                columnText = new ColumnText(layer);
                columnText.setSimpleColumn(0, 0, width, height);
                columnText.addElement(paragraph);
                columnText.go();
                break;
        }

    }

}
