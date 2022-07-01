import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTest {
    private MedicalServiceImpl medicalService;
    private SendAlertService sendAlertService;
    private PatientInfo patientInfo;

    @BeforeEach
    public void init() {
        patientInfo = new PatientInfo("1", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("41"), new BloodPressure(120, 80)));

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(patientInfo.getId())).thenReturn(patientInfo);
        Mockito.when(patientInfoRepository.add(Mockito.any(PatientInfo.class))).thenReturn(patientInfo.getId());

        sendAlertService = Mockito.mock(SendAlertService.class);

        medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);


    }

    /**
     * Проверка отправки уведомления о повышенной температуре
     */
    @Test
    public void sendingNotificationTemperature() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("36"));
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());

        Assertions.assertNotNull(argumentCaptor.getValue());

    }

    /**
     * Проверка отправки уведомления о повышенном давлении
     */
    @Test
    public void sendingNotificationBloodPressure() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(150, 120));
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());

        Assertions.assertNotNull(argumentCaptor.getValue());

    }

    /**
     * Проверка кол-ва вызовов метода
     */
    @Test
    public void invokeCountTemperature() {
        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("36"));
        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
    }

    /**
     * Проверка кол-ва вызовов метода
     */
    @Test
    public void invokeCountBloodPleasure() {
        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(150, 120));

        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());

    }


}
