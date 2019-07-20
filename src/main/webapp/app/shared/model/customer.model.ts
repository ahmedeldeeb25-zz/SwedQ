import { IVehicle } from 'app/shared/model/vehicle.model';

export interface ICustomer {
    id?: number;
    name?: string;
    address?: string;
    notes?: string;
    vehicles?: IVehicle[];
}

export class Customer implements ICustomer {
    constructor(public id?: number, public name?: string, public address?: string, public notes?: string, public vehicles?: IVehicle[]) {}
}
