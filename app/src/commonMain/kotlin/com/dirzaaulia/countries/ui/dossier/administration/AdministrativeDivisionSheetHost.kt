package com.dirzaaulia.countries.ui.dossier.administration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dirzaaulia.countries.domain.country.AdminLevel
import com.dirzaaulia.countries.domain.country.AdministrativeDivision
import com.dirzaaulia.countries.domain.country.Country
import com.dirzaaulia.countries.ui.dossier.administration.AdministrativeDivisionSheet
import com.dirzaaulia.countries.ui.globe.AdministrativeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdministrativeDivisionSheetHost(
    country: Country,
    onClose: () -> Unit,
    onCenterDivision: (AdministrativeDivision) -> Unit,
) {
    val viewModel: AdministrativeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(country.id) {
        viewModel.resetAdmState()
        viewModel.loadAdmDivisions(country)
    }

    AdministrativeDivisionSheet(
        country = country,
        administrativeDivisions = uiState.administrativeDivisions,
        isFetchingAdministrativeDivisions = uiState.isFetchingAdministrativeDivisions,
        selectedAdministrativeDivision = uiState.selectedAdministrativeDivision,
        administrativeLevel = uiState.administrativeLevel,
        selectedAdm1Division = uiState.selectedAdm1Division,
        onClose = {
            viewModel.resetAdmState()
            onClose()
        },
        onLoadAdministrativeDivisions = {
            viewModel.loadAdmDivisions(country, uiState.administrativeLevel)
        },
        onShowAdm1 = {
            viewModel.resetAdmState()
            viewModel.loadAdmDivisions(country, AdminLevel.ADM1)
        },
        onSelectAdministrativeDivision = { division ->
            viewModel.selectAdministrativeDivision(division)
            if (division.level == AdminLevel.ADM1) {
                viewModel.loadAdmDivisions(country, AdminLevel.ADM2)
            }
        },
        onCenterDivision = { division ->
            viewModel.selectAdministrativeDivision(division)
            onCenterDivision(division)
        },
    )
}
