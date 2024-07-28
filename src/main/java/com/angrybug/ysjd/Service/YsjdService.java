package com.angrybug.ysjd.Service;

import com.angrybug.ysjd.DTO.BriefPatientInfo;
import com.angrybug.ysjd.Entity.Patient;
import com.angrybug.ysjd.Entity.Simulation;
import com.angrybug.ysjd.Repository.PatientRepository;
import com.angrybug.ysjd.Repository.SimulationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.SerializationFeature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class YsjdService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private QRService qrService;

    @Value("${service.api3url}")
    private String QRURL;


    public Patient createPatient(
            String name, String birthdate, Long sex, Long weight, Long height, String bloodPressure, String pastDiseases, String currentMedications, String allergies, String familyHistory, String symtoms, String onset, Long painSize
    )
    {
        Patient patient = new Patient();
        patient.setName(name);

        Date sqlBirthDate = parseSqlDate(birthdate);
        patient.setBirthdate(sqlBirthDate);

        String sexStr = parseSex(sex);
        patient.setSex(sexStr);

        patient.setWeight(weight);
        patient.setHeight(height);
        patient.setBloodPressure(bloodPressure);
        patient.setPastDiseases(pastDiseases);
        patient.setCurrentMedications(currentMedications);
        patient.setAllergies(allergies);
        patient.setFamilyHistory(familyHistory);
        patient.setSymptoms(symtoms);
        patient.setOnset(onset);
        patient.setPainLevel(painSize);

        return patientRepository.save(patient);
    }

    private Date parseSqlDate(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate;

        int year = Integer.parseInt(birthdate.substring(0, 2));
        String centuryPrefix = (year >= 0 && year <= 24) ? "20" : "19";

        try {
            localDate = LocalDate.parse(centuryPrefix+birthdate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + birthdate);
        }


        return Date.valueOf(localDate);
    }

    private String parseSex(Long sex) {

        String sexStr = null;

        if(sex.equals(1L) || sex.equals(3L)) {
            sexStr = "male";
        }
        else if(sex.equals(2L) || sex.equals(4L)) {
            sexStr = "female";
        }

        return sexStr;
    }

    public List<BriefPatientInfo> findPatientList() {

        List<Patient> patientList = patientRepository.findAll();

        List<BriefPatientInfo> briefPatientInfoList = new ArrayList<>();

        for (Patient patient : patientList) {
            String parsedDate = parseOriginDate(patient.getBirthdate());
            BriefPatientInfo briefPatientInfo = new BriefPatientInfo(patient.getPatientId(), patient.getName(), parsedDate, patient.getSex());
            briefPatientInfoList.add(briefPatientInfo);
        }
        return briefPatientInfoList;
    }


    private String parseOriginDate(Date birthdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(birthdate);
    }

    public Optional<Patient> findPatientInfo(Long id) {
        return patientRepository.findById(id);
    }

    public String createSimulation(Long id, String name) {

        Optional<Patient> patient = patientRepository.findById(id);

        if(patient.isPresent()){
            try{
                String simul = simulationService.callExternalApi1(convertObjectToJson(patient)).block();
                simul = parsingSimulationOutput(simul);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                JsonNode jsonNode = objectMapper.readTree(simul);

                // JsonNode를 ObjectNode로 변환하여 수정 가능하게 함
                if (jsonNode instanceof ObjectNode objectNode) {
                    // ObjectNode를 JSON 문자열로 변환하여 반환
                    return objectMapper.writeValueAsString(objectNode);
                }

                return null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }



    public String convertObjectToJson(Optional<Patient> optionalPatient) {
        try {

            // 값이 있는 경우 해당 값을 사용하고, 없는 경우 람다 표현식으로 기본값 제공
            Patient patient = optionalPatient.orElseGet(() -> null);

            Date date = null;

            try{
                date = patient.getBirthdate();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // java.sql.Date을 yyyy-MM-dd 형식의 String으로 변환
            String formattedDate = sdf.format(date);


            // User 객체를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonString = objectMapper.writeValueAsString(patient);

            // JSON 문자열을 JsonNode로 변환
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // JsonNode를 ObjectNode로 변환하여 수정 가능하게 함
            if (jsonNode instanceof ObjectNode objectNode) {
                // "id" 필드 제거
                objectNode.remove("patientId");
                objectNode.put("birthdate", formattedDate);

                // ObjectNode를 JSON 문자열로 변환하여 반환
                return objectMapper.writeValueAsString(objectNode);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String saveSimulationResult(Long id, String scenario) {

        Simulation simulation = new Simulation();
        Simulation savedSimulation = null;

        // Frame 설정
        if (id != null) {
            Optional<Patient> patientOpt = patientRepository.findById(id);
            patientOpt.ifPresent(simulation::setPatientId);
        }

        simulation.setScenario(scenario);

        //해당 환자가 이미 simulation 결과가 있다면 update
        List<Simulation> simulationList = simulationRepository.findByPatientId(simulation.getPatientId().getPatientId());

        System.out.println("hello");
        System.out.println(simulationList);

        if(!simulationList.isEmpty()){
            System.out.println(simulationList.getFirst().getPatientId());

            //해당 환자 update()
            Simulation existingSimulation = simulationList.getLast();
            existingSimulation.setScenario(scenario);

            simulationRepository.save(existingSimulation);

            System.out.println("hello2");

        }
        //해당 환자가 simulation 결과가 없으면 save
        else{
            simulationRepository.save(simulation);

            System.out.println("hello3");
        }

        return "Success";

    }

    private byte[] createQRCode(Long id){
        String url = QRURL + id;
        int width = 200;
        int height = 200;

        try {
            BufferedImage qrImage = qrService.generateQrCode(url, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageData = baos.toByteArray();

            return imageData;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createSelectedSimulation(Long id, Long explainType) {

        Optional<Patient> optionalPatient = patientRepository.findById(id);

        //Simulation테이블에서 PatientId칼럼이 id와 일치하는 SelectedSimulation 데이터 가져오기
        List<Simulation> simulationList = simulationRepository.findByPatientId(id);

        //Patient정보와 SelectedSimulation 정보 합치기

        try{

            Patient patient = optionalPatient.orElseGet(() -> null);
            String patientName = patient.getName();

            Simulation simulation = simulationList.get(0);

            String combinedRequestBody = combineRequestBody(patient, simulation, explainType);

            String simul = simulationService.callExternalApi2(combinedRequestBody).block();

            simul = parsingSimulationOutput(simul);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode jsonNode = objectMapper.readTree(simul);

            // JsonNode를 ObjectNode로 변환하여 수정 가능하게 함
            if (jsonNode instanceof ObjectNode objectNode) {

                objectNode.put("name", patientName);

                // ObjectNode를 JSON 문자열로 변환하여 반환
                return objectMapper.writeValueAsString(objectNode);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private String combineRequestBody(Patient patient, Simulation simulation, Long explainType) {

        try {

            Date date = patient.getBirthdate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(date);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonString = objectMapper.writeValueAsString(patient);
            String scenario = simulation.getScenario();

            // JSON 문자열을 JsonNode로 변환
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // JsonNode를 ObjectNode로 변환하여 수정 가능하게 함
            if (jsonNode instanceof ObjectNode objectNode) {
                // "id" 필드 제거
                objectNode.remove("patientId");
                objectNode.put("birthdate", formattedDate);
                objectNode.put("explainType", explainType);
                objectNode.put("scenario", objectMapper.readTree(scenario));

                // ObjectNode를 JSON 문자열로 변환하여 반환
                return objectMapper.writeValueAsString(objectNode);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String parsingSimulationOutput(String input) {
        int openBraces = 0;
        StringBuilder strbuilder = new StringBuilder();
        boolean foundOpening = false;

        for (char c : input.toCharArray()) {
            if (c == '{') {
                openBraces++;
                foundOpening = true;
            }
            if (foundOpening) {
                strbuilder.append(c);
            }
            if (c == '}') {
                openBraces--;
                if (openBraces == 0) {
                    break;
                }
            }
        }

        String result = strbuilder.toString();
        result = result.replaceAll("\\\\", "");
        System.out.println(result);

        result = result.replaceAll("\\bn\\b", "");

        return result;
    }




}
