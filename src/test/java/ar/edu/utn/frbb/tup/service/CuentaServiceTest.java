package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private CuentaService cuentaService;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cuentaService.clienteService = clienteService;
    }

    @Test
    public void testCuentaExistente() {

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(12345678);

        when(cuentaDao.find(12345678)).thenReturn(cuenta);

        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 12345678));
    }

    @Test
    public void testDarDeAltaCuentaNoSoportada() {
        // Arrange
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.DOLARES)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        // Act & Assert
        assertThrows(CuentaNoSoportadaException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 12345678));
    }

    @Test
    public void testClienteTieneCuentaDelMismoTipo() throws TipoCuentaAlreadyExistsException {

        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuentaCajaAhorro = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);

        clienteService.agregarCuenta(cuentaCajaAhorro, pepeRino.getDni());

        Cuenta cuentaCajaAhorroDuplicada = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(10000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        assertThrows(TipoCuentaAlreadyExistsException.class,
                () -> cuentaService.darDeAltaCuenta(cuentaCajaAhorroDuplicada, pepeRino.getDni()));
    }

    @Test
    public void testCuentaCreadaExitosamente() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        // Arrange
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuentaCajaAhorro = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);

        // Act
        cuentaService.darDeAltaCuenta(cuentaCajaAhorro, pepeRino.getDni());

        // Assert
        verify(clienteDao, times(1)).find(26456439, true);
        verify(cuentaDao, times(1)).save(cuentaCajaAhorro);
        assertEquals(1, pepeRino.getCuentas().size());
        assertTrue(pepeRino.getCuentas().contains(cuentaCajaAhorro));
        assertEquals(pepeRino, cuentaCajaAhorro.getTitular());
    }

}