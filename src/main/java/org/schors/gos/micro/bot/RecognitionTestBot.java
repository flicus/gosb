package org.schors.gos.micro.bot;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.schors.gos.micro.model.BattleLayout;
import org.schors.gos.micro.model.Player;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.repository.BattleRepositoryDbImpl;
import org.schors.gos.micro.repository.PlayerRepositoryDbImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecognitionTestBot extends TelegramLongPollingBot {

  private final PlayerRepositoryDbImpl playerRepository;
  private final BattleRepositoryDbImpl battleRepository;
  private PlayerLayout playerLayout = null;
  private List<Player> players = null;

  public RecognitionTestBot(PlayerRepositoryDbImpl playerRepository, BattleRepositoryDbImpl battleRepository) {
    this.playerRepository = playerRepository;
    this.battleRepository = battleRepository;
  }

  private static PlayerLayout recognize(File file) {

    System.out.println(file.getAbsolutePath());
    Mat img = Imgcodecs.imread(file.getAbsolutePath());

    Mat grey = new Mat();
    Imgproc.cvtColor(img, grey, Imgproc.COLOR_BGR2GRAY);
    Mat initialGrey = grey.clone();

    Imgproc.adaptiveThreshold(grey, grey, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
    int horizontal_size = grey.cols() / 2;
    Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontal_size, 1));
    Imgproc.dilate(grey, grey, Mat.ones(2, 2, CvType.CV_8U));
    Imgproc.erode(grey, grey, horizontalStructure);
    Imgproc.dilate(grey, grey, horizontalStructure);
    Core.bitwise_not(grey, grey);

    Mat hierarchy = new Mat();
    List<MatOfPoint> list = new ArrayList<>();
    Imgproc.findContours(grey, list, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

    Tesseract tesseract = new Tesseract();
    tesseract.setDatapath("/home/flicus/tera/java/tessdata_best-main");
    tesseract.setLanguage("eng");
    tesseract.setTessVariable("tessedit_char_whitelist", "0123456789BMKT.");
    tesseract.setPageSegMode(8);

    List<String> strings = new ArrayList<>();

    list.stream()
      .map(matOfPoint -> Imgproc.boundingRect(matOfPoint))
      .map(rect -> initialGrey.submat(rect))
      .filter(mat -> mat.height() > 100)
      .forEach(first -> {
        Mat res = first.clone();
        Imgproc.medianBlur(first, first, 5);

        Mat circles = new Mat();
        Imgproc.HoughCircles(first, circles, Imgproc.HOUGH_GRADIENT, 1.0,
          (double) first.rows() / 16, // change this value to detect circles with different distances to each other
          100.0, 30.0, 20, 40); // change the last two parameters
        Mat mask = Mat.zeros(first.rows(), first.cols(), CvType.CV_8U);
        double mx = 0;
        int mr = 0;
        for (int x = 0; x < circles.cols(); x++) {
          double[] c = circles.get(0, x);
          mx = Math.max(mx, Math.round(c[1]));
          mr = Math.max(mr, (int) Math.round(c[2]));
          Point center = new Point(Math.round(c[0]), Math.round(c[1]));
          int radius = (int) Math.round(c[2]);
          Imgproc.circle(first, center, radius + 2, new Scalar(255, 0, 255), -1, 8, 0);
        }
        Imgproc.rectangle(mask, new Point(0, 0), new Point(mask.cols(), mx + mr + 2), new Scalar(255, 255, 255), -1);
        Imgproc.rectangle(first, new Point(0, 0), new Point(first.cols(), mx + mr + 2), new Scalar(255, 255, 255), -1);
        Core.bitwise_not(mask, mask);

        Mat result = new Mat();
        res.copyTo(result, mask);
        Mat r_i = res.clone();

        Imgproc.threshold(result, result, 140, 255, Imgproc.THRESH_BINARY /*+ Imgproc.THRESH_OTSU*/);
        Mat hh = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 3));
        Imgproc.dilate(result, result, hh);

        Mat hierarchy2 = new Mat();
        List<MatOfPoint> list2 = new ArrayList<>();
        Imgproc.findContours(result, list2, hierarchy2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        list2.stream()
          .map(matOfPoint -> Imgproc.boundingRect(matOfPoint))
          .filter(rect -> rect.width > rect.height * 2 && rect.height > 10)
          .map(rect -> new Rect(rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 3))
          .forEach(rect -> {
            Imgproc.rectangle(r_i, rect, new Scalar(0, 255, 0), 1);
            Mat m = r_i.submat(rect);
            Imgproc.dilate(m, m, Mat.ones(1, 1, CvType.CV_8U));
            Core.bitwise_not(m, m);
            BufferedImage bimg = null;
            try {
              bimg = mat2BufferedImage(m);
            } catch (Exception e) {
              e.printStackTrace();
            }
            String s = null;
            try {
              s = tesseract.doOCR(bimg);
            } catch (TesseractException e) {
              e.printStackTrace();
            }
            System.out.println(s);
            strings.add(s.trim());
          });
      });
    PlayerLayout playerLayout = new PlayerLayout();
    playerLayout.setB2(new BattleLayout(strings.get(13), strings.get(12), strings.get(15), strings.get(14)));
    playerLayout.setB3(new BattleLayout(strings.get(9), strings.get(8), strings.get(11), strings.get(10)));
    playerLayout.setB5(new BattleLayout(strings.get(5), strings.get(4), strings.get(7), strings.get(6)));
    playerLayout.setB7(new BattleLayout(strings.get(1), strings.get(0), strings.get(3), strings.get(2)));

//    ParseResult parseResult = new ParseResult();
//    parseResult.setRec7(new String[]{strings.get(0), strings.get(1), strings.get(2), strings.get(3)});
//    parseResult.setRec5(new String[]{strings.get(4), strings.get(5), strings.get(6), strings.get(7)});
//    parseResult.setRec3(new String[]{strings.get(8), strings.get(9), strings.get(10), strings.get(11)});
//    parseResult.setRec2(new String[]{strings.get(12), strings.get(13), strings.get(14), strings.get(15)});

    return playerLayout;

  }

  static BufferedImage mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".png", matrix, mob);
    return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
  }

  public static <T> Stream<Stream<T>> getTuples(Collection<T> items, int size) {
    int page = 0;
    Stream.Builder<Stream<T>> builder = Stream.builder();
    Stream<T> stream;
    do {
      stream = items.stream().skip(size * page++).limit(size);
      builder.add(stream);
    } while (items.size() - size * page > 0);
    return builder.build();
  }

  @Override
  public String getBotUsername() {
    return "pangos_bot";
  }

  @Override
  public String getBotToken() {
    return "1059091004:AAHsbBr9bHhziXHap_5VI4Djbo9UEi_3hi8";
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasPhoto()) {
      PhotoSize photo = update.getMessage().getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize)).get();
      String url = null;
      if (photo.getFilePath() != null) { // If the file_path is already present, we are done!
        url = photo.getFilePath();
      } else { // If not, let find it
        // We create a GetFile method and set the file_id from the photo
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(photo.getFileId());
        try {
          // We execute the method using AbsSender::execute method.
          org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
          // We now have the file_path
          url = file.getFilePath();
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }
      try {
        File file = this.downloadFile(url);
        playerLayout = recognize(file);
        execute(SendMessage.builder()
          .chatId(String.valueOf(update.getMessage().getChatId()))
          .text(playerLayout.readable()).build());
        players = playerRepository.getAllPlayers();
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();
        getTuples(players, 3).
          forEach(tuple -> inlineKeyboardMarkupBuilder
            .keyboardRow(tuple.map(player -> InlineKeyboardButton.builder()
                .text(player.getName())
                .callbackData(player.getId())
                .build())
              .collect(Collectors.toList())));
        execute(
          SendMessage.builder()
            .chatId(String.valueOf(update.getMessage().getChatId()))
            .text("Чья расстановка?")
            .replyMarkup(inlineKeyboardMarkupBuilder.build())
            .build());

      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    } else if (update.hasCallbackQuery()) {
      try {
        execute(AnswerCallbackQuery.builder()
          .callbackQueryId(update.getCallbackQuery().getId())
          .build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
      if (playerLayout != null) {
        String id = update.getCallbackQuery().getData();
        playerLayout.setPlayer(players.stream().filter(player -> player.getId().equals(id)).findAny().get());
        battleRepository.addPlayerLayout(playerLayout);
        playerLayout = null;
        players = null;
      }
//      try {
//        execute(SendMessage.builder().chatId(String.valueOf(update.getCallbackQuery().getChatId())).text("Добавлено").build());
//      } catch (TelegramApiException e) {
//        e.printStackTrace();
//      }
    } else {
      try {
        execute(SendMessage.builder()
          .chatId(String.valueOf(update.getMessage().getChatId()))
          .text("Изображение не найдено")
          .build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    }
  }
}
