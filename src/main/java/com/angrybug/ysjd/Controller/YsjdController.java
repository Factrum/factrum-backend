package com.angrybug.ysjd.Controller;

import com.angrybug.ysjd.DTO.*;
import com.angrybug.ysjd.Entity.Patient;
import com.angrybug.ysjd.Service.YsjdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.*;

@RestController
public class YsjdController {

    @Autowired
    private YsjdService ysjdService;

//    @GetMapping("/hello")
//    public String Test() {
//        System.out.println("hello");
//        return "good";
//    }
//
//
//    @PostMapping("/hello")
//    public String Test2(@RequestBody Test test){
//        System.out.println(test.name);
//        System.out.println(test.father);
//        System.out.println(test.mother);
//        return "good2";
//    }


    //API1. 환자 정보 입력
    @PostMapping("/patient/info")
    public ResponseEntity<String> createPatient(@RequestBody PatientInfo patientInfo)
    {
        Patient patient = ysjdService.createPatient(
                        patientInfo.getName(),
                        patientInfo.getBirthdate(),
                        patientInfo.getGenderNumber(),
                        patientInfo.getWeight(),
                        patientInfo.getHeight(),
                        patientInfo.getBloodPressure(),
                        patientInfo.getPastDiseases(),
                        patientInfo.getCurrentMedications(),
                        patientInfo.getAllergies(),
                        patientInfo.getFamilyHistory(),
                        patientInfo.getSymptoms(),
                        patientInfo.getOnset(),
                        patientInfo.getPainLevel()
        );

        return ResponseEntity.ok("Success");
    }

    //API2. 환자 리스트 반환
    @GetMapping("/patient/list")
    public ResponseEntity<Map<String, List<BriefPatientInfo>>> findPatientsList()
    {
        List<BriefPatientInfo> patientList = ysjdService.findPatientList();

        Map<String, List<BriefPatientInfo>> response = new HashMap<>();
        response.put("patientList", patientList);

        return ResponseEntity.ok(response);
    }

    //API3. 환자 리스트 반환
    @GetMapping("/simulation/info/{id}")
    public ResponseEntity<Optional<Patient>> findPatientInfo(@PathVariable Long id)
    {

        Optional<Patient> patient = ysjdService.findPatientInfo(id);

        if (patient.isPresent()) {
            return ResponseEntity.ok(patient);
        }
        else {
            return ResponseEntity.ok(null);
        }
    }

    //API4. 시뮬레이션 생성
    @PostMapping("/simulation/result")
    public ResponseEntity<String> createSimulation(@RequestBody SimulationInfo simulationInfo)
    {

        String simulationResult = ysjdService.createSimulation(simulationInfo.getId(), simulationInfo.getName());

        return ResponseEntity.ok(simulationResult);
    }

    //API5. 시뮬레이션 선택 결과저장
    @PostMapping("/simulation/selected")
    public ResponseEntity<byte[]> saveSimulationResult(@RequestBody String jsonData) {

        try {
            // JSON 문자열을 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            SimulationResult simulationResult = objectMapper.readValue(jsonData, SimulationResult.class);

            // DTO 객체의 test 필드를 JSON 문자열로 설정
            String tempScenario = objectMapper.writeValueAsString(simulationResult.getScenario());

            byte[] qrcode = ysjdService.saveSimulationResult(simulationResult.getId(), tempScenario);

            writeToFile("test", qrcode);


            return ResponseEntity.ok(qrcode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public void writeToFile(String filename, byte[] pData){
        if(pData == null){
            return;
        }
        int lByteArraySize = pData.length;
        System.out.println(filename);
        try{
            File lOutFile = new File("./"+filename+".jpg");
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.write(pData);
            lFileOutputStream.close();
        }catch(Throwable e){
            e.printStackTrace(System.out);
        }
    }

    //API6. 선별된 시나리오 설명 생성
    @PostMapping("/simulation/patient")
    public ResponseEntity<String> createSelectedSimulation(@RequestBody SelectedSimulationResultRequest selectedSimulationResultRequest) {


        String simulationResult = ysjdService.createSelectedSimulation(selectedSimulationResultRequest.getId(), selectedSimulationResultRequest.getExplainType());

        return ResponseEntity.ok(simulationResult);



    }




}
