export interface Skill {
    id: number;
    name: string;
}

export interface FreelancerProfile {
    id: number;
    nickname: string;
    profileImageUrl?: string;
    introduction: string;
    location: string;
    hourlyRate: number;
    workStyle: 'ONLINE' | 'OFFLINE' | 'HYBRID';
    skills: Skill[];
    averageRating: number;
}