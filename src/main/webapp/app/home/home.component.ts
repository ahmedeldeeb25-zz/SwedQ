import { VehicleService } from './../entities/vehicle/vehicle.service';
import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, AccountService, Account } from 'app/core';
import { Subject, Subscription, Observable } from 'rxjs';

import { IVehicle } from 'app/shared/model/vehicle.model';
import { setInterval } from 'timers';

declare var $;

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    dtOptions: DataTables.Settings = {};
    dtTrigger: Subject<any> = new Subject();
    vehicles: IVehicle[];

    constructor(
        private accountService: AccountService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private vehicleService: VehicleService
    ) {}

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();

        this.dtOptions = {
            pagingType: 'full_numbers',
            order: [0, 'asc'],
            lengthMenu: [5, 10, 25, 50],
            info: false
            // dom: 'ltipr'
        };

        this.vehicleService.query().subscribe(res => {
            console.log(res.body);
            this.vehicles = res.body;
            this.dtTrigger.next();
        });

        setInterval(() => {
            //  const table = $('.table').DataTable();
            //  table.destroy();
            this.vehicleService.query().subscribe(res => {
                console.log(res.body);
                this.vehicles = res.body;
                // this.dtTrigger.next();
            });
        }, 10000);

        $(document).ready(function() {
            // $('#table').DataTable();
        });

        setTimeout(() => {
            $('.alert-success').css('display', 'none');
        }, 9000);
    }

    trackId(index: number, item: IVehicle) {
        return item.id;
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.accountService.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
