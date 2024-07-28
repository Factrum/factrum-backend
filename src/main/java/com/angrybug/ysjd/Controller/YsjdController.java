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

    @GetMapping("/test")
    public String test()
    {
        return "test success";
    }


    //API1. 환자 정보 입력
    @PostMapping("/patient/info")
    public ResponseEntity<String> createPatient(@RequestBody PatientInfo patientInfo)
    {

        try{
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
        catch(Exception e){
            return ResponseEntity.ok("error");
        }

    }

    //API2. 환자 리스트 반환
    @GetMapping("/patient/list")
    public ResponseEntity<Map<String, List<BriefPatientInfo>>> findPatientsList()
    {
        try {
            List<BriefPatientInfo> patientList = ysjdService.findPatientList();

            Map<String, List<BriefPatientInfo>> response = new HashMap<>();
            response.put("patientList", patientList);

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.ok(null);
        }
    }

    //API3. 환자 리스트 반환
    @GetMapping("/simulation/info/{id}")
    public ResponseEntity<Optional<Patient>> findPatientInfo(@PathVariable Long id)
    {

        try{
            Optional<Patient> patient = ysjdService.findPatientInfo(id);

            if (patient.isPresent()) {
                return ResponseEntity.ok(patient);
            }
            else {
                return ResponseEntity.ok(null);
            }
        }
        catch (Exception e) {
            return ResponseEntity.ok(null);
        }

    }

    //API4. 시뮬레이션 생성
    @PostMapping("/simulation/result")
    public ResponseEntity<String> createSimulation(@RequestBody SimulationInfo simulationInfo)
    {
        String simulationResult = null;

        try{
            simulationResult = ysjdService.createSimulation(simulationInfo.getId(), simulationInfo.getName());

            return ResponseEntity.ok(simulationResult);
        }catch(Exception e){
            return ResponseEntity.ok("error");
        }

    }

    //API5. 시뮬레이션 선택 결과저장
    @PostMapping("/simulation/selected")
    public ResponseEntity<String> saveSimulationResult(@RequestBody String jsonData) {

        try {
            // JSON 문자열을 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            SimulationResult simulationResult = objectMapper.readValue(jsonData, SimulationResult.class);

            // DTO 객체의 test 필드를 JSON 문자열로 설정
            String tempScenario = objectMapper.writeValueAsString(simulationResult.getScenario());

            String result  = ysjdService.saveSimulationResult(simulationResult.getId(), tempScenario);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return null;
        }

    }

    //API6. 선별된 시나리오 설명 생성
    @PostMapping("/simulation/patient")
    public ResponseEntity<String> createSelectedSimulation(@RequestBody SelectedSimulationResultRequest selectedSimulationResultRequest) {

        String simulationResult = null;

        try{
            simulationResult = ysjdService.createSelectedSimulation(selectedSimulationResultRequest.getId(), selectedSimulationResultRequest.getExplainType());

            return ResponseEntity.ok(simulationResult);
        }
        catch(Exception e){
            return ResponseEntity.ok("error");
        }

    }

}
