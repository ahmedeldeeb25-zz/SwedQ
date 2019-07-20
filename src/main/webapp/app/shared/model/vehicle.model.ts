import { ICustomer } from 'app/shared/model/customer.model';

export interface IVehicle {
    id?: number;
    reg?: string;
    status?: string;
    online?: boolean;
    time?: string;
    coordinates?: string;
    customer?: ICustomer;
}

export class Vehicle implements IVehicle {
    constructor(
        public id?: number,
        public reg?: string,
        public status?: string,
        public online?: boolean,
        public time?: string,
        public coordinates?: string,
        public customer?: ICustomer
    ) {
        this.online = this.online || false;
    }
}
