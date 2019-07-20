import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'customer',
                loadChildren: './customer/customer.module#TestproCustomerModule'
            },
            {
                path: 'vehicle',
                loadChildren: './vehicle/vehicle.module#TestproVehicleModule'
            },
            {
                path: 'customer',
                loadChildren: './customer/customer.module#TestproCustomerModule'
            },
            {
                path: 'vehicle',
                loadChildren: './vehicle/vehicle.module#TestproVehicleModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TestproEntityModule {}
