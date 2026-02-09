export interface Beneficio {
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
  version?: number;
}

export interface BeneficioCreateDTO {
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
}

export interface BeneficioUpdateDTO {
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
}

export interface TransferenciaDTO {
  fromId: number;
  toId: number;
  amount: number;
}
